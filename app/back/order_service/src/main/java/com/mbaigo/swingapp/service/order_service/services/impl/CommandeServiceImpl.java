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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final CatalogServiceClient catalogClient;
    private final CommandeMapper commandeMapper;

    @Override
    public CommandeResponse createCommande(CommandeRequest request) {
        Commande commande = initCommandeBase(request);
        Map<Long, CatalogArticleDTO> catalogueArticles = fetchArticlesEnBatch(request.materiaux());
        List<LigneMateriauCommande> lignes = buildLignesMateriaux(commande, request.materiaux(), catalogueArticles);

        commande.getMateriaux().addAll(lignes);
        commande.calculerPrixTotal();

        Commande savedCommande = commandeRepository.save(commande);
        return commandeMapper.toResponse(savedCommande);
    }

    private Commande initCommandeBase(CommandeRequest request) {
        // FeignException (ex: 404 Not Found) sera interceptée globalement si le modèle n'existe pas.
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
        if (materiauxReq == null || materiauxReq.isEmpty()) {
            // Sécurité supplémentaire même si le DTO a @NotEmpty
            throw new IllegalArgumentException("La commande doit contenir au moins un matériau.");
        }

        List<Long> articleIds = materiauxReq.stream()
                .map(LigneMateriauCommandeRequest::articleId)
                .distinct()
                .collect(Collectors.toList());

        List<CatalogArticleDTO> articlesDuCatalogue = catalogClient.getArticlesInBatch(articleIds);

        return articlesDuCatalogue.stream()
                .collect(Collectors.toMap(CatalogArticleDTO::id, article -> article));
    }

    private List<LigneMateriauCommande> buildLignesMateriaux(Commande commande,
                                                             List<LigneMateriauCommandeRequest> materiauxReq,
                                                             Map<Long, CatalogArticleDTO> articleMap) {
        return materiauxReq.stream().map(reqLigne -> {
            CatalogArticleDTO article = articleMap.get(reqLigne.articleId());

            if (article == null) {
                throw new IllegalArgumentException("Impossible de créer la commande. L'article avec l'ID " + reqLigne.articleId() + " n'existe pas dans le catalogue.");
            }

            return LigneMateriauCommande.builder()
                    .commande(commande)
                    .articleId(article.id())
                    .quantite(reqLigne.quantite())
                    .prixUnitaireSnapshot(article.prixAchat())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeResponse getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La commande avec l'ID " + id + " est introuvable."));
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
                .orElseThrow(() -> new IllegalArgumentException("La commande avec l'ID " + id + " est introuvable."));

        if (commande.getStatut() == StatutCommande.ANNULEE || commande.getStatut() == StatutCommande.TERMINEE) {
            throw new IllegalStateException("Opération impossible : le statut d'une commande déjà '" + commande.getStatut() + "' ne peut pas être modifié.");
        }

        if (nouveauStatut == StatutCommande.ANNULEE) {
            throw new IllegalArgumentException("Pour annuler une commande, vous devez utiliser la procédure de Rollback dédiée (annulerCommande).");
        }

        commande.setStatut(nouveauStatut);
        Commande savedCommande = commandeRepository.save(commande);
        return commandeMapper.toResponse(savedCommande);
    }

    @Override
    public CommandeResponse annulerCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La commande avec l'ID " + id + " est introuvable."));

        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new IllegalStateException("Cette commande a déjà été annulée.");
        }

        List<RestockItemRequest> elementsARecrediter = commande.getMateriaux().stream()
                .map(ligne -> new RestockItemRequest(ligne.getArticleId(), ligne.getQuantite()))
                .collect(Collectors.toList());

        if (!elementsARecrediter.isEmpty()) {
            // Si cette ligne échoue (ex: 500 sur le catalogue), une FeignException sera levée.
            // La transaction sera annulée (Rollback local), et le ControllerAdvice renverra une 500 propre au client.
            catalogClient.restockArticles(elementsARecrediter);
        }

        commande.setStatut(StatutCommande.ANNULEE);
        Commande savedCommande = commandeRepository.save(commande);

        return commandeMapper.toResponse(savedCommande);
    }

    @Transactional(readOnly = true)
    public List<CommandeResponse> getRendezVousParPeriode(String periode) {
        BornesPeriode bornes = calculerBornesPeriode(periode);

        List<Commande> commandes = commandeRepository
                .findByDateRendezVousBetweenOrderByDateRendezVousAsc(bornes.debut(), bornes.fin());

        return commandes.stream()
                .map(commandeMapper::toResponse)
                .toList();
    }

    // --- MÉTHODES PRIVÉES ---

    // Le record utilise maintenant LocalDate
    private record BornesPeriode(LocalDate debut, LocalDate fin) {}

    private BornesPeriode calculerBornesPeriode(String periode) {
        LocalDate today = LocalDate.now();

        return switch (periode.toLowerCase()) {
            case "journalier" ->
                // Pour aujourd'hui, le début et la fin sont le même jour !
                    new BornesPeriode(today, today);

            case "semaine" -> {
                LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
                LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
                yield new BornesPeriode(startOfWeek, endOfWeek);
            }

            case "mois" -> {
                LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
                yield new BornesPeriode(startOfMonth, endOfMonth);
            }

            default -> throw new IllegalArgumentException("Période de filtre non reconnue : " + periode);
        };
    }
}