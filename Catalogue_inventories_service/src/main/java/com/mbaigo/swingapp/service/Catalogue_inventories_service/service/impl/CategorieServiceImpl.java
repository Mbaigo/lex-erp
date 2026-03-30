package com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.CategorieMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.CategorieRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;
    private final CategorieMapper categorieMapper;

    @Override
    public CategorieResponse createCategorie(CategorieRequest request) {
        // Règle métier : le code doit être unique
        if (categorieRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Une catégorie avec le code " + request.code() + " existe déjà.");
        }

        // 1. Conversion DTO -> Entité
        Categorie categorie = categorieMapper.toEntity(request);

        // 2. Sauvegarde en base
        Categorie savedCategorie = categorieRepository.save(categorie);

        // 3. Conversion Entité -> DTO pour la réponse
        return categorieMapper.toResponse(savedCategorie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorieResponse> getAllCategories() {
        return categorieRepository.findAll()
                .stream()
                .map(categorieMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategorieResponse getCategorieById(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable avec l'ID : " + id));
        return categorieMapper.toResponse(categorie);
    }

    @Override
    public CategorieResponse updateCategorie(Long id, CategorieRequest request) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable avec l'ID : " + id));

        // Règle métier : Vérifier que le nouveau code n'est pas déjà pris par UNE AUTRE catégorie
        if (!categorie.getCode().equals(request.code()) && categorieRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Le code " + request.code() + " est déjà utilisé par une autre catégorie.");
        }

        // MapStruct met à jour l'entité existante avec les nouvelles données
        categorieMapper.updateEntityFromRequest(request, categorie);

        Categorie updatedCategorie = categorieRepository.save(categorie);
        return categorieMapper.toResponse(updatedCategorie);
    }
}
