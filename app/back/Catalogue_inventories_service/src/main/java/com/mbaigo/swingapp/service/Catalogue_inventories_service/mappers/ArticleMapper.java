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

    @Mapping(target = "categorie.id", source = "categorieId") // Le lien magique
    @Mapping(target = "id", ignore = true) // L'ID est géré par la BDD
    @Mapping(target = "version", ignore = true) // La version est gérée par JPA pour l'US 3.3
    Article toEntity(ArticleRequest request);

    ArticleResponse toResponse(Article entity);

    @Mapping(target = "categorie.id", source = "categorieId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(ArticleRequest request, @MappingTarget Article entity);
}
