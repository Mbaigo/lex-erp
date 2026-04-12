package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.RestockItemRequest;

import java.util.List;

public interface ArticleService {

    // US 3.1 - Entrée en stock (Création)
    ArticleResponse createArticle(ArticleRequest request);

    // US 3.2 - Alertes de rupture
    List<ArticleResponse> getArticlesEnAlerte();

    // US 3.3 - Débit/Crédit sécurisé
    ArticleResponse updateStock(String reference, Double quantite, boolean isDebit);

    // Méthodes classiques utiles
    List<ArticleResponse> getAllArticles();
    ArticleResponse getArticleById(Long id);

    List<ArticleResponse> getArticlesByIds(List<Long> ids);
    //Recreditation du stock en cas d'annulation d'une commande
    void restockBatch(List<RestockItemRequest> requests);
    void deleteArticle(Long id);
}
