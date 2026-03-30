package com.mbaigo.swingapp.service.customer.customer_service.dto;

import java.time.LocalDate;
import java.util.Map;

public record FicheMesureResponseDTO(
        Long id,
        Long clientId,
        String nomProjet,
        LocalDate datePrise,
        Map<String, Double> valeurs
) {}
