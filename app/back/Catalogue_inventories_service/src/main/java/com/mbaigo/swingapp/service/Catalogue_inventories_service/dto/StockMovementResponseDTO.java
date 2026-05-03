package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.TypeMovementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockMovementResponseDTO(

        Long id,

        Long articleId,

        String articleReference,

        Integer quantite,

        TypeMovementEnum type,

        BigDecimal prixUnitaire,

        String motif,

        LocalDate dateOperation,

        LocalDateTime createdAt,
        Integer stockApresOperation

) {}
