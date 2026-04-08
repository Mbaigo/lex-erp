package com.mbaigo.swingapp.service.order_service.services.impl;

import com.mbaigo.swingapp.service.order_service.clientDto.CatalogArticleDTO;
import com.mbaigo.swingapp.service.order_service.clientDto.CatalogModeleDTO;
import com.mbaigo.swingapp.service.order_service.clientService.CatalogServiceClient;
import com.mbaigo.swingapp.service.order_service.dto.CommandeRequest;
import com.mbaigo.swingapp.service.order_service.dto.CommandeResponse;
import com.mbaigo.swingapp.service.order_service.dto.LigneMateriauCommandeRequest;
import com.mbaigo.swingapp.service.order_service.dto.reStock.RestockItemRequest;
import com.mbaigo.swingapp.service.order_service.entities.Commande;
import com.mbaigo.swingapp.service.order_service.entities.LigneMateriauCommande;
import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;
import com.mbaigo.swingapp.service.order_service.mappers.CommandeMapper;
import com.mbaigo.swingapp.service.order_service.repositories.CommandeRepository;
import com.mbaigo.swingapp.service.order_service.services.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final CatalogServiceClient catalogClient; // Notre pont vers le microservice Catalogue !
    private final CommandeMapper commandeMapper; // Je le commente pour l'instant, on le fera avec MapStruct

    @Override
    public CommandeResponse createCommande(CommandeRequest request) {
        // 1. Initialiser la commande avec les données du Modèle
        Commande commande = initCommandeBase(request);

        // 2. Optimisation Réseau : Récupérer tous les articles en 1 seule requête
        Map<Long, CatalogArticleDTO> catalogueArticles = fetchArticlesEnBatch(request.materiaux());

        // 3. Construire les lignes avec les snapshots de prix
        List<LigneMateriauCommande> lignes = buildLignesMateriaux(commande, request.materiaux(), catalogueArticles);
        commande.getMateriaux().addAll(lignes);

        // 4. Calculer le total et sauvegarder
        commande.calculerPrixTotal();
        Commande savedCommande = commandeRepository.save(commande);

        return commandeMapper.toResponse(savedCommande);
    }

    // =========================================================
    // MÉTHODES PRIVÉES DE FACTORISATION
    // =========================================================

    private Commande initCommandeBase(CommandeRequest request) {
        CatalogModeleDTO modele = catalogClient.getModeleById(request.modeleId());
        return Commande.builder()
                .reference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .clientId(request.clientId())
                .modeleId(modele.id())
                .coutMainOeuvreSnapshot(modele.coutMainOeuvre())
                .statut(StatutCommande.CREEE)
                .build();
    }

    private Map<Long, CatalogArticleDTO> fetchArticlesEnBatch(List<LigneMateriauCommandeRequest> materiauxReq) {
        // Extraction des IDs uniques
        List<Long> articleIds = materiauxReq.stream()
                .map(LigneMateriauCommandeRequest::articleId)
                .distinct()
                .collect(Collectors.toList());

        // Appel Feign unique
        List<CatalogArticleDTO> articlesDuCatalogue = catalogClient.getArticlesInBatch(articleIds);

        // Conversion en Dictionnaire (Map) pour une recherche ultra-rapide
        return articlesDuCatalogue.stream()
                .collect(Collectors.toMap(CatalogArticleDTO::id, article -> article));
    }

    private List<LigneMateriauCommande> buildLignesMateriaux(Commande commande,
                                                             List<LigneMateriauCommandeRequest> materiauxReq,
                                                             Map<Long, CatalogArticleDTO> articleMap) {
        return materiauxReq.stream().map(reqLigne -> {
            CatalogArticleDTO article = articleMap.get(reqLigne.articleId());

            if (article == null) {
                throw new IllegalArgumentException("L'article avec l'ID " + reqLigne.articleId() + " n'existe pas dans le catalogue.");
            }

            return LigneMateriauCommande.builder()
                    .commande(commande) // Lien parent
                    .articleId(article.id())
                    .quantite(reqLigne.quantite())
                    .prixUnitaireSnapshot(article.prixAchat()) // Snapshot figé
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeResponse getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable avec l'ID : " + id));
        return commandeMapper.toResponse(commande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeResponse> getAllCommandes() {
        return commandeRepository.findAll()
                .stream()
                .map(commandeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommandeResponse updateStatut(Long id, StatutCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable."));

        // Règle métier : On ne modifie pas le statut d'une commande annulée ou déjà terminée
        if (commande.getStatut() == StatutCommande.ANNULEE || commande.getStatut() == StatutCommande.TERMINEE) {
            throw new IllegalStateException("Impossible de modifier le statut d'une commande " + commande.getStatut());
        }

        // Règle métier : Si on veut annuler, on force à utiliser la méthode dédiée au rollback
        if (nouveauStatut == StatutCommande.ANNULEE) {
            throw new IllegalArgumentException("Pour annuler une commande, veuillez utiliser l'action d'annulation (qui gère les stocks).");
        }

        commande.setStatut(nouveauStatut);
        Commande savedCommande = commandeRepository.save(commande);
        return commandeMapper.toResponse(savedCommande);
    }

    @Override
    public CommandeResponse annulerCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable."));

        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new IllegalStateException("Cette commande est déjà annulée.");
        }

        // US 6.2 : LE ROLLBACK DISTRIBUÉ
        // 1. On prépare la liste de tous les articles et quantités à rendre au stock
        List<RestockItemRequest> elementsARecrediter = commande.getMateriaux().stream()
                .map(ligne -> new RestockItemRequest(ligne.getArticleId(), ligne.getQuantite()))
                .collect(Collectors.toList());

        // 2. On appelle le microservice Catalogue via Feign pour faire le restock
        if (!elementsARecrediter.isEmpty()) {
            catalogClient.restockArticles(elementsARecrediter);
        }

        // 3. Si le catalogue a répondu sans erreur (200 OK), on annule notre commande
        commande.setStatut(StatutCommande.ANNULEE);
        Commande savedCommande = commandeRepository.save(commande);

        return commandeMapper.toResponse(savedCommande);
    }

}
