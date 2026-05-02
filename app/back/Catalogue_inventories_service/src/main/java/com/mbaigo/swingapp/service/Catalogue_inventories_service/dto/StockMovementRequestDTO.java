package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockMovementRequestDTO(

        Long articleId, // 🔥 important pour lier l'article

        Integer quantite,

        Boolean isDebit, // true = sortie, false = entrée

        BigDecimal prixUnitaire,

        String motif,

        LocalDate dateOperation

) {}
