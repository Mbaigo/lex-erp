package com.mbaigo.swingapp.service.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CommandeRequest(
        @NotNull(message = "Le client est obligatoire")
        Long clientId,

        @NotNull(message = "Le modèle de base est obligatoire")
        Long modeleId,

        @NotEmpty(message = "La commande doit contenir au moins un matériau")
        @Valid
        List<LigneMateriauCommandeRequest> materiaux
) {}
