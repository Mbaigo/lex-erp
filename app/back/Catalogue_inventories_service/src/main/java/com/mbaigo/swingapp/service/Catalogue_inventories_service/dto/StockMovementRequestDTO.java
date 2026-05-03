package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.TypeMovementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockMovementRequestDTO(

        Long articleId, // 🔥 important pour lier l'article

        Integer quantite,

        TypeMovementEnum type, // true = sortie, false = entrée

        BigDecimal prixUnitaire,

        String motif,

        LocalDate dateOperation,
        Integer stockAvantOperation

) {}
