package com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    boolean existsByReference(String reference);

    Optional<Article> findByReference(String reference);

    // US 3.2 : Trouver les articles en alerte de rupture
    @Query("SELECT a FROM Article a WHERE a.quantiteEnStock <= a.seuilAlerte")
    List<Article> findArticlesEnAlerte();
}
