package com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockCategorieDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    // Vérifie si un code existe déjà
    boolean existsByCode(String code);

    // Récupère une catégorie via son code
    Optional<Categorie> findByCode(String code);

    @Query("""
        SELECT new com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockCategorieDTO(
            c.id,
            c.nom,
            COALESCE(SUM(a.stockActuel), 0L),
            COUNT(a.id)
        )
        FROM Categorie c
        LEFT JOIN Article a ON a.categorie = c
        GROUP BY c.id, c.nom
    """)
    List<StockCategorieDTO> getStockParCategorie();
}
