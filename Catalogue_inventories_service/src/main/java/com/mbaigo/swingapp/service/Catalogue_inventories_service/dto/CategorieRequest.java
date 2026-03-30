package  com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategorieRequest(

        @NotBlank(message = "Le code de la catégorie est obligatoire")
        @Size(min = 2, max = 50, message = "Le code doit contenir entre 2 et 50 caractères")
        String code,

        @NotBlank(message = "Le nom de la catégorie est obligatoire")
        @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
        String nom,

        @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
        String description
) {}