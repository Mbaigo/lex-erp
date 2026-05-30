package com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementRequestDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementResponseDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.StockMovement;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {

    // =========================================================
    // 1. REQUEST -> ENTITY (Création d'un mouvement)
    // =========================================================
    @Mapping(target = "article.id", source = "articleId") // Reconstitue la relation
    @Mapping(target = "id", ignore = true)                // Géré par la BDD
    @Mapping(target = "createdAt", ignore = true)         // Géré par la BDD ou @PrePersist
    @Mapping(target = "priceTotal", ignore = true)        // Sera calculé dans le @AfterMapping
    StockMovement toEntity(StockMovementRequestDTO request);

    // =========================================================
    // 2. ENTITY -> RESPONSE (Lecture d'un mouvement)
    // =========================================================
    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "articleReference", source = "article.reference")
    @Mapping(target = "articleDesignation", source = "article.designation")
    StockMovementResponseDTO toResponse(StockMovement entity);

    // =========================================================
    // 3. LOGIQUE MÉTIER AUTOMATIQUE
    // =========================================================
    /**
     * MapStruct va appeler cette méthode automatiquement juste après avoir
     * exécuté toEntity(). C'est l'endroit parfait pour calculer le coût total.
     */
    @AfterMapping
    default void calculatePriceTotal(StockMovementRequestDTO request, @MappingTarget StockMovement entity) {
        if (request.quantite() != null && request.prixUnitaire() != null) {
            // Note : Je pars du principe que tu as suivi le conseil de passer priceTotal en BigDecimal
            BigDecimal total = request.prixUnitaire().multiply(new BigDecimal(request.quantite()));
            entity.setPriceTotal(total);
        } else {
            entity.setPriceTotal(BigDecimal.ZERO);
        }
    }
}