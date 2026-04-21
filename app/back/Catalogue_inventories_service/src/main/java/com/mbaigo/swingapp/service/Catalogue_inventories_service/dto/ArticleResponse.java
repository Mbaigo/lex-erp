package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record ArticleResponse(
        Long id,
        String reference,
        String designation,
        Double quantiteEnStock,
        BigDecimal prixAchat,
        Double seuilAlerte,
        CategorieResponse categorie // On imbrique la réponse de la catégorie !
) {}
