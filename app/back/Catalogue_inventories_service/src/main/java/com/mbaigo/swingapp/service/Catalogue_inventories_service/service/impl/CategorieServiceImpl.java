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
        if (categorieRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Impossible de créer la catégorie : le code '" + request.code() + "' existe déjà.");
        }
        Categorie categorie = categorieMapper.toEntity(request);
        return categorieMapper.toResponse(categorieRepository.save(categorie));
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
                .orElseThrow(() -> new IllegalArgumentException("La catégorie avec l'ID " + id + " est introuvable."));
        return categorieMapper.toResponse(categorie);
    }

    @Override
    public CategorieResponse updateCategorie(Long id, CategorieRequest request) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mise à jour impossible : la catégorie avec l'ID " + id + " est introuvable."));

        if (!categorie.getCode().equals(request.code()) && categorieRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Le code '" + request.code() + "' est déjà utilisé par une autre catégorie.");
        }

        categorieMapper.updateEntityFromRequest(request, categorie);
        return categorieMapper.toResponse(categorieRepository.save(categorie));
    }

    // NOUVEAU : Méthode de suppression
    @Override
    public void deleteCategorie(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : la catégorie avec l'ID " + id + " n'existe pas.");
        }
        // Si la catégorie contient encore des articles, JPA va jeter une DataIntegrityViolationException.
        // Le GlobalExceptionHandler la captera (409 Conflict).
        categorieRepository.deleteById(id);
    }
}