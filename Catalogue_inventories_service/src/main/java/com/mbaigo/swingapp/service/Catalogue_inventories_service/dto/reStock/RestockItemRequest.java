package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock;

import jakarta.validation.constraints.NotNull;

public record RestockItemRequest(
        @NotNull Long articleId,
        @NotNull Double quantite
) {}