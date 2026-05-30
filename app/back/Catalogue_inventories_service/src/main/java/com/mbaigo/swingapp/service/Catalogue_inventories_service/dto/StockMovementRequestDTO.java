package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.TypeMovementEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record StockMovementRequestDTO(

        // Note : Si ton URL est déjà /articles/{reference}/stock,
        // tu peux retirer ce champ car l'article est déduit de l'URL.
        @NotNull(message = "L'ID de l'article est obligatoire")
        Long articleId,

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité doit être au moins de 1")
        Integer quantite,

        @NotNull(message = "Le type de mouvement (ENTREE/SORTIE) est obligatoire")
        TypeMovementEnum type,

        @PositiveOrZero(message = "Le prix unitaire ne peut pas être négatif")
        BigDecimal prixUnitaire, // Optionnel, surtout utile pour les ENTREE (achats)

        @Size(max = 255, message = "Le motif ne doit pas dépasser 255 caractères")
        String motif,

        LocalDate dateOperation // Si null, le backend l'initialisera à LocalDate.now()
) {}
