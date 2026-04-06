package com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.ArticleMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ArticleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.CategorieRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository; // Besoin pour vérifier si la catégorie existe
    private final ArticleMapper articleMapper;

    @Override
    public ArticleResponse createArticle(ArticleRequest request) {
        // 1. Vérifier que la référence est unique
        if (articleRepository.existsByReference(request.reference())) {
            throw new IllegalArgumentException("Un article avec la référence " + request.reference() + " existe déjà.");
        }

        // 2. Vérifier que la catégorie existe
        Categorie categorie = categorieRepository.findById(request.categorieId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable avec l'ID : " + request.categorieId()));

        // 3. Mapper et sauvegarder
        Article article = articleMapper.toEntity(request);
        article.setCategorie(categorie); // On s'assure que la relation est bien faite

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
    public ArticleResponse updateStock(String reference, Double quantite, boolean isDebit) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité à modifier doit être strictement positive.");
        }

        Article article = articleRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable avec la référence : " + reference));

        if (isDebit) {
            // US 3.3 : Bloquer si le stock est insuffisant
            if (article.getQuantiteEnStock() < quantite) {
                throw new IllegalStateException("Stock insuffisant. Stock actuel : " + article.getQuantiteEnStock());
            }
            article.setQuantiteEnStock(article.getQuantiteEnStock() - quantite);
        } else {
            // Crédit (Ajout en stock)
            article.setQuantiteEnStock(article.getQuantiteEnStock() + quantite);
        }

        // La sauvegarde déclenchera la vérification @Version automatiquement (Optimistic Locking)
        Article updatedArticle = articleRepository.save(article);
        return articleMapper.toResponse(updatedArticle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll()
                .stream()
                .map(articleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable avec l'ID : " + id));
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

}
