package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieResponse;

import java.util.List;

public interface CategorieService {

    CategorieResponse createCategorie(CategorieRequest request);

    List<CategorieResponse> getAllCategories();

    CategorieResponse getCategorieById(Long id);
    CategorieResponse updateCategorie(Long id, CategorieRequest request);
}
