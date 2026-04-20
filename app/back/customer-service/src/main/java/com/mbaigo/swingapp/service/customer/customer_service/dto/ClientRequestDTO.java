package com.mbaigo.swingapp.service.customer.customer_service.dto;

import com.mbaigo.swingapp.service.customer.customer_service.enums.Genre;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientRequestDTO(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        String prenom,

        @NotBlank(message = "Le téléphone est obligatoire")
        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Format de téléphone invalide")
        String telephone,

        @Email(message = "Format d'email invalide")
        String email,


        Genre genre,

        String notesMorphologie
) {}
