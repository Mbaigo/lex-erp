package com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

// On précise "uses = {CategorieMapper.class}" pour que MapStruct sache
// comment convertir l'entité Categorie en CategorieResponse
@Mapper(componentModel = "spring", uses = {CategorieMapper.class})
public interface ArticleMapper {

    @Mapping(target = "categorie.id", source = "categorieId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    // On mappe le stock reçu UNIQUEMENT dans le stockInitial
    //@Mapping(target = "stockActuel", ignore = true)
    @Mapping(target = "stockActuel", source = "stockInitial")
    @Mapping(target = "stockInitial", source = "stockInitial")
    Article toEntity(ArticleRequest request);

    // Pour renvoyer les données au frontend, on lit le stockActuel
//    @Mapping(target = "quantiteStock", source = "stockActuel")
//    @Mapping(target = "categorieId", source = "categorie.id")
//    @Mapping(target = "categorieNom", source = "categorie.nom")
    ArticleResponse toResponse(Article entity);

    @Mapping(target = "categorie.id", source = "categorieId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    // Lors d'une mise à jour (US d'édition), on met à jour uniquement le seuil, prix, etc.
    // MAIS ON IGNORE LE STOCK pour ne pas écraser les mouvements de stock !
    @Mapping(target = "stockActuel", ignore = true)
    @Mapping(target = "stockInitial", ignore = true)
    void updateEntityFromRequest(ArticleRequest request, @MappingTarget Article entity);
}
