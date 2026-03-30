package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneNomenclatureRequest(
        @NotNull(message = "L'ID de l'article est obligatoire")
        Long articleId,

        @NotNull(message = "La quantité nécessaire est obligatoire")
        @Min(value = 0, message = "La quantité doit être positive")
        Double quantiteNecessaire
) {}
