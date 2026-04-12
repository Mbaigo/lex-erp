package com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    // Vérifie si un code existe déjà
    boolean existsByCode(String code);

    // Récupère une catégorie via son code
    Optional<Categorie> findByCode(String code);
}
