package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockMovementResponseDTO(

        Long id,

        Long articleId,

        String articleReference,

        Integer quantite,

        Boolean isDebit,

        BigDecimal prixUnitaire,

        String motif,

        LocalDate dateOperation,

        LocalDateTime createdAt

) {}
