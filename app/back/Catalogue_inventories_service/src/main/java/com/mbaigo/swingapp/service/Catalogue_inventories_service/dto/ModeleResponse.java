package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record ModeleResponse(
        Long id,
        String reference,
        String nom,
        String description,
        BigDecimal coutMainOeuvre,
        BigDecimal coutDeBase, // Calculé dynamiquement (Main d'œuvre + Total des lignes)
        List<LigneNomenclatureResponse> lignesNomenclature
) {}
