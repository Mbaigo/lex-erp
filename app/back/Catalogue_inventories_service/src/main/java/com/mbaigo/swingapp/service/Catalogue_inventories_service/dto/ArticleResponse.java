package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.UniteMesureEnum;
import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record ArticleResponse(
        Long id,
        String reference,
        String designation,
        Long stockInitial,
        Long stockActuel,
        BigDecimal prixUnitaire,
        Double seuilAlerte,
        Boolean enAlerte,
        UniteMesureEnum uniteMesure,
        CategorieResponse categorie // On imbrique la réponse de la catégorie !
) {}
