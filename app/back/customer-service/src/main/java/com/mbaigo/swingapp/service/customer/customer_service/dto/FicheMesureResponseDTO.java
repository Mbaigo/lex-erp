package com.mbaigo.swingapp.service.customer.customer_service.dto;

import java.time.LocalDate;
import java.util.Map;

public record FicheMesureResponseDTO(
        Long id,
        Long clientId,
        // Ajoute ces deux lignes :
        String clientNom,
        String clientPrenom,
        String nomProjet,
        LocalDate datePrise,
        Map<String, Double> mesures,
        String remarquesSpecifiques
) {}
