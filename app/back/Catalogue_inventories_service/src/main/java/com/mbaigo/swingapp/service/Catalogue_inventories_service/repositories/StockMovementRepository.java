package com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // 🔥 Historique d’un article (ordre chronologique inverse)
    Page<StockMovement> findByArticleIdOrderByDateOperationDesc(Long articleId, Pageable pageable);
    Page<StockMovement> findByArticleId(Long articleId, Pageable pageable);
    Page<StockMovement> findByArticleDesignationContainingIgnoreCase(
            String designation,
            Pageable pageable
    );

    @Query("""
    SELECT sm FROM StockMovement sm
    WHERE (:reference IS NULL OR sm.article.reference = :reference)
      AND (:designation IS NULL OR LOWER(sm.article.designation) LIKE LOWER(CONCAT('%', :designation, '%')))
""")
    Page<StockMovement> searchMovements(
            String reference,
            String designation,
            Pageable pageable
    );

}
