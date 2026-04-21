package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Le DTO pour le Body
public record StockMovementRequest(
        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être supérieure à zéro")
        Double quantite,

        @NotNull
        boolean isDebit,

        String motif
) {}// Ajout futur facile

