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
            throw new IllegalArgumentException("Un modèle avec la référence " + request.reference() + " existe déjà.");
        }

        Modele modele = modeleMapper.toEntity(request);

        // Lier chaque ligne de nomenclature à son modèle parent et charger l'article réel
        modele.getLignesNomenclature().forEach(ligne -> {
            ligne.setModele(modele); // Bidirectionnalité indispensable pour Hibernate

            // On s'assure que l'article existe et on le charge pour pouvoir calculer le coût de base
            Article article = articleRepository.findById(ligne.getArticle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Article introuvable avec l'ID : " + ligne.getArticle().getId()));
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
                .orElseThrow(() -> new IllegalArgumentException("Modèle introuvable avec l'ID : " + id));
        return modeleMapper.toResponse(modele);
    }

    @Override
    public ModeleResponse updateModele(Long id, ModeleRequest request) {
        Modele modele = modeleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modèle introuvable avec l'ID : " + id));

        // Vérification de la référence unique
        if (!modele.getReference().equals(request.reference()) && modeleRepository.existsByReference(request.reference())) {
            throw new IllegalArgumentException("La référence " + request.reference() + " est déjà utilisée.");
        }

        // 1. Astuce JPA : On vide la nomenclature actuelle pour forcer le "orphanRemoval"
        // à supprimer les anciennes lignes de la base de données.
        modele.getLignesNomenclature().clear();

        // 2. On applique les nouvelles valeurs (MapStruct va ajouter les nouvelles lignes dans la liste)
        modeleMapper.updateEntityFromRequest(request, modele);

        // 3. On doit refaire le lien bidirectionnel parent-enfant comme à la création !
        modele.getLignesNomenclature().forEach(ligne -> {
            ligne.setModele(modele);
            Article article = articleRepository.findById(ligne.getArticle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Article introuvable avec l'ID : " + ligne.getArticle().getId()));
            ligne.setArticle(article);
        });

        Modele updatedModele = modeleRepository.save(modele);
        return modeleMapper.toResponse(updatedModele);
    }
}
