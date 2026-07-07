package com.mbaigo.swingapp.service.customer.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

public record FicheMesureRequestDTO(
        @NotNull(message = "L'ID du client est obligatoire")
        Long clientId,

        @NotBlank(message = "Le nom du projet est obligatoire (ex: Costume Mariage)")
        String nomProjet,

//        @NotNull(message = "La date de prise de mesure est obligatoire")
//        LocalDate datePrise,

        // C'est ici que la magie opère. Le frontend enverra un objet JSON libre.
        @NotNull(message = "Les valeurs des mesures sont obligatoires")
        Map<String, Double> mesures,
        String remarquesSpecifiques
) {}
