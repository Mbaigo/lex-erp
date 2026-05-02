package com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementRequestDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementResponseDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.StockMovement;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    // 🔥 REQUEST → ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "article", source = "articleId", qualifiedByName = "mapArticle")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    StockMovement toEntity(StockMovementRequestDTO dto);

    // 🔥 ENTITY → RESPONSE
    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "articleReference", source = "article.reference")
    StockMovementResponseDTO toDTO(StockMovement entity);

    // 🔥 LIST
    List<StockMovementResponseDTO> toDTOList(List<StockMovement> entities);


    // 🔥 CUSTOM MAPPING ARTICLE
    @Named("mapArticle")
    default Article mapArticle(Long articleId) {
        if (articleId == null) return null;

        Article article = new Article();
        article.setId(articleId);
        return article;
    }
}
