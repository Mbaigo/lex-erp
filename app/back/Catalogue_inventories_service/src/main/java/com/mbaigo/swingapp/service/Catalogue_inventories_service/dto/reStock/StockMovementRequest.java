package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

// Le DTO pour le Body
public record StockMovementRequest(
        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être supérieure à zéro")
        Double quantite,
        Double prixUnitaire,

        @NotNull
        boolean isDebit,
        @NotNull @Pattern(regexp = "jj/mm/aaaa")
        LocalDate dateStock,

        String motif
) {}// Ajout futur facile

