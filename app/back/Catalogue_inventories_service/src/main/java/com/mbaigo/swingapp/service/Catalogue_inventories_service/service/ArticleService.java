package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {

    // US 3.1 - Entrée en stock (Création)
    ArticleResponse createArticle(ArticleRequest request);

    // US 3.2 - Alertes de rupture
    List<ArticleResponse> getArticlesEnAlerte();


    // Méthodes classiques utiles
    Page<ArticleResponse> getAllArticles(int page, int size);
    ArticleResponse getArticleById(Long id);

    List<ArticleResponse> getArticlesByIds(List<Long> ids);
    void deleteArticle(Long id);
    Page<ArticleResponse> getArticlesByCategorie(
            Long categorieId,
            Pageable pageable
    );
}
