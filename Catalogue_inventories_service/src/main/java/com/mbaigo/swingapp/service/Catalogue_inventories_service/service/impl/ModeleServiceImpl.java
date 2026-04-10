package com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Modele;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.ModeleMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ArticleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ModeleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ModeleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ModeleServiceImpl implements ModeleService {

    private final ModeleRepository modeleRepository;
    private final ArticleRepository articleRepository;
    private final ModeleMapper modeleMapper;

    @Override
    public ModeleResponse createModele(ModeleRequest request) {
        if (modeleRepository.existsByReference(request.reference())) {
            throw new IllegalArgumentException("Impossible de créer le modèle : la référence '" + request.reference() + "' existe déjà.");
        }

        Modele modele = modeleMapper.toEntity(request);

        modele.getLignesNomenclature().forEach(ligne -> {
            ligne.setModele(modele);
            Article article = articleRepository.findById(ligne.getArticle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Création du modèle impossible : l'article avec l'ID " + ligne.getArticle().getId() + " n'existe pas dans le catalogue."));
            ligne.setArticle(article);
        });

        Modele savedModele = modeleRepository.save(modele);
        return modeleMapper.toResponse(savedModele);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModeleResponse> getAllModeles() {
        return modeleRepository.findAll()
                .stream()
                .map(modeleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ModeleResponse getModeleById(Long id) {
        Modele modele = modeleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Le modèle avec l'ID " + id + " est introuvable."));
        return modeleMapper.toResponse(modele);
    }

    @Override
    public ModeleResponse updateModele(Long id, ModeleRequest request) {
        Modele modele = modeleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mise à jour impossible : le modèle avec l'ID " + id + " est introuvable."));

        if (!modele.getReference().equals(request.reference()) && modeleRepository.existsByReference(request.reference())) {
            throw new IllegalArgumentException("La référence '" + request.reference() + "' est déjà utilisée par un autre modèle.");
        }

        modele.getLignesNomenclature().clear();
        modeleMapper.updateEntityFromRequest(request, modele);

        modele.getLignesNomenclature().forEach(ligne -> {
            ligne.setModele(modele);
            Article article = articleRepository.findById(ligne.getArticle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Mise à jour du modèle impossible : l'article avec l'ID " + ligne.getArticle().getId() + " n'existe pas."));
            ligne.setArticle(article);
        });

        Modele updatedModele = modeleRepository.save(modele);
        return modeleMapper.toResponse(updatedModele);
    }

    // NOUVEAU : Méthode de suppression
    @Override
    public void deleteModele(Long id) {
        if (!modeleRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : le modèle avec l'ID " + id + " n'existe pas.");
        }
        // Grâce au orphanRemoval = true dans ton entité Modele,
        // la suppression du modèle supprimera proprement ses lignes de nomenclature en cascade.
        modeleRepository.deleteById(id);
    }
}