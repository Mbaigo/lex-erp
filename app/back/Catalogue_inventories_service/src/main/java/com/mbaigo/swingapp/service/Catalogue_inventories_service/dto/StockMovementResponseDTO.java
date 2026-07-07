package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.TypeMovementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockMovementResponseDTO(
        Long id,

        // --- Infos de l'article aplaties pour le Frontend ---
        Long articleId,
        String articleReference,
        String articleDesignation,

        // --- Infos du mouvement ---
        Integer quantite,
        TypeMovementEnum type,
        BigDecimal prixUnitaire,
        BigDecimal priceTotal,
        Integer stockAvantOperation,
        Integer stockApresOperation,
        String motif,
        LocalDate dateOperation,
        LocalDateTime createdAt
) {}
