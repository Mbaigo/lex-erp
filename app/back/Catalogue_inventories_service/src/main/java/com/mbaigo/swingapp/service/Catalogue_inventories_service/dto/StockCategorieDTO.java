package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

public record StockCategorieDTO(
        Long categorieId,
        String categorieNom,
        Long stockTotal,
        Long nombreArticles
) {}
