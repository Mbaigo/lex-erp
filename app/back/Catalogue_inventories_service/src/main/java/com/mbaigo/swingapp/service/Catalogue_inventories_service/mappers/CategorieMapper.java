package com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategorieMapper {

    // Convertir la requête du client en Entité pour la base de données
    Categorie toEntity(CategorieRequest request);

    // Convertir l'Entité de la base de données en réponse pour le client
    CategorieResponse toResponse(Categorie entity);

    // Mettre à jour une entité existante à partir d'une requête
    void updateEntityFromRequest(CategorieRequest request, @MappingTarget Categorie entity);
}