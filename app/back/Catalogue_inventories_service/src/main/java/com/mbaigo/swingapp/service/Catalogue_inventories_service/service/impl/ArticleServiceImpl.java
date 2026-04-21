package com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.RestockItemRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.StockMovementRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.ArticleMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ArticleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.CategorieRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;
    private final ArticleMapper articleMapper;

    @Override
    public ArticleResponse createArticle(ArticleRequest request) {
        if (articleRepository.existsByReference(request.reference())) {
            throw new IllegalArgumentException("Impossible de créer l'article : la référence '" + request.reference() + "' existe déjà.");
        }

        Categorie categorie = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new IllegalArgumentException("Impossible de lier l'article : la catégorie avec l'ID " + request.categorieId() + " n'existe pas."));

        Article article = articleMapper.toEntity(request);
        article.setCategorie(categorie);

        Article savedArticle = articleRepository.save(article);
        return articleMapper.toResponse(savedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesEnAlerte() {
        return articleRepository.findArticlesEnAlerte()
                .stream()
                .map(articleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArticleResponse updateStock(String reference, StockMovementRequest stock) {
        if (stock.quantite() <= 0) {
            throw new IllegalArgumentException("La quantité à modifier doit être strictement positive.");
        }

        Article article = articleRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Mouvement de stock impossible : l'article avec la référence '" + reference + "' est introuvable."));

        if (stock.isDebit()) {
            if (article.getQuantiteEnStock() < stock.quantite()) {
                // Intercepté par le GlobalExceptionHandler -> Renvoie une 409 Conflict
                throw new IllegalStateException("Stock insuffisant pour l'article '" + reference + "'. Stock actuel : " + article.getQuantiteEnStock());
            }
            article.setQuantiteEnStock(article.getQuantiteEnStock() - stock.quantite());
        } else {
            article.setQuantiteEnStock(article.getQuantiteEnStock() + stock.quantite());
        }

        Article updatedArticle = articleRepository.save(article);
        return articleMapper.toResponse(updatedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getAllArticles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return articleRepository.findAll(pageable)
                .map(articleMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("L'article avec l'ID " + id + " est introuvable."));
        return articleMapper.toResponse(article);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesByIds(List<Long> ids) {
        return articleRepository.findAllById(ids)
                .stream()
                .map(articleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void restockBatch(List<RestockItemRequest> requests) {
        for (RestockItemRequest req : requests) {
            Article article = articleRepository.findById(req.articleId())
                    .orElseThrow(() -> new IllegalArgumentException("Restockage échoué : Article introuvable (ID: " + req.articleId() + ")"));

            article.setQuantiteEnStock(article.getQuantiteEnStock() + req.quantite());
            articleRepository.save(article);
        }
    }

    // NOUVEAU : Méthode de suppression
    @Override
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new IllegalArgumentException("Impossible de supprimer : l'article avec l'ID " + id + " n'existe pas.");
        }
        // Si l'article est utilisé dans une Nomenclature de Modèle, JPA va jeter une DataIntegrityViolationException.
        // Le GlobalExceptionHandler la transformera en message propre pour le Gérant.
        articleRepository.deleteById(id);
    }
}