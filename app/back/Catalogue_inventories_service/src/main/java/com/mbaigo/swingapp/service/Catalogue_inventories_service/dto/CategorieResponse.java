package  com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

public record CategorieResponse(
        Long id,
        String code,
        String nom,
        String description
) {}