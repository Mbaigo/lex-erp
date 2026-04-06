package com.mbaigo.swingapp.service.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneMateriauCommandeRequest(
        @NotNull(message = "L'ID de l'article est obligatoire")
        Long articleId, // Le tissu choisi (par défaut ou substitué)

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 0, message = "La quantité doit être positive")
        Double quantite
) {}
