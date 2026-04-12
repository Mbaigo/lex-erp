package com.mbaigo.swingapp.service.customer.customer_service.dto;

import com.mbaigo.swingapp.service.customer.customer_service.enums.Genre;

import java.time.LocalDateTime;

public record ClientResponseDTO(
        Long id,
        String nom,
        String prenom,
        String telephone,
        String email,
        Genre genre,
        String notesMorphologie,
        LocalDateTime dateCreation,
        String adresse
) {}
