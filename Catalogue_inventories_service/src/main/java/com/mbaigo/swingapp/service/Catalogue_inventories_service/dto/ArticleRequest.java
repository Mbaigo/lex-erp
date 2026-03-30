package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ArticleRequest(

        @NotBlank(message = "La référence est obligatoire")
        @Size(max = 50, message = "La référence ne peut pas dépasser 50 caractères")
        String reference, // Ex: BOUTON-NACRE-15

        @NotBlank(message = "La désignation est obligatoire")
        @Size(max = 150)
        String designation, // Ex: Lot de boutons en nacre 15mm

        @NotNull(message = "La quantité en stock est obligatoire")
        @Min(value = 0, message = "Le stock initial ne peut pas être négatif")
        Double quantiteEnStock,

        @NotNull(message = "Le prix d'achat est obligatoire")
        @Min(value = 0, message = "Le prix d'achat ne peut pas être négatif")
        BigDecimal prixAchat,

        @NotNull(message = "Le seuil d'alerte est obligatoire")
        @Min(value = 0, message = "Le seuil d'alerte ne peut pas être négatif")
        Double seuilAlerte,

        @NotNull(message = "L'ID de la catégorie est obligatoire")
        Long categorieId // Le client envoie juste l'ID de la catégorie (ex: 1 pour "Mercerie")
) {}
