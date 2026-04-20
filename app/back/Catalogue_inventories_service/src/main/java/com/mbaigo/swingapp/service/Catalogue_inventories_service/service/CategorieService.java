package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategorieService {

    CategorieResponse createCategorie(CategorieRequest request);

    Page<CategorieResponse> getAllCategories(int page, int size);

    CategorieResponse getCategorieById(Long id);
    CategorieResponse updateCategorie(Long id, CategorieRequest request);
    void deleteCategorie(Long id);
}
