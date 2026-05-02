package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record ArticleResponse(
        Long id,
        String reference,
        String designation,
        Double stockInitial,
        BigDecimal prixUnitaire,
        Double seuilAlerte,
        Boolean enAlerte,
        String uniteMesure,
        CategorieResponse categorie // On imbrique la réponse de la catégorie !
) {}
