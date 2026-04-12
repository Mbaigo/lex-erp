package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ModeleRequest(
        @NotBlank(message = "La référence du modèle est obligatoire")
        String reference,

        @NotBlank(message = "Le nom du modèle est obligatoire")
        String nom,

        String description,

        @NotNull(message = "Le coût de main-d'œuvre est obligatoire")
        @Min(value = 0, message = "Le coût de main-d'œuvre ne peut pas être négatif")
        BigDecimal coutMainOeuvre,

        @NotEmpty(message = "Un modèle doit contenir au moins un article dans sa nomenclature")
        @Valid
        List<LigneNomenclatureRequest> lignes,

        @NotEmpty(message = "Un modèle doit contenir au moins une image dans sa nomenclature")
        @Valid
        String imageUrl
) {}
