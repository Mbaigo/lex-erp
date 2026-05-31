package com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementRequestDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementResponseDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.StockMovement;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.TypeMovementEnum;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.StockMovementMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ArticleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.StockMovementRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository movementRepository;
    private final ArticleRepository articleRepository;
    private final StockMovementMapper mapper;

    // 🟢 CRÉATION D'UN MOUVEMENT
    @Transactional
    @Override
    public StockMovementResponseDTO createMovement(StockMovementRequestDTO request) {

            // 1. Récupération de l'article
            Article article = articleRepository.findById(request.articleId())
                    .orElseThrow(() -> new RuntimeException("Article introuvable avec l'ID: " + request.articleId()));

            if (request.quantite() == null || request.quantite() <= 0) {
                throw new RuntimeException("Quantité invalide : " + request.quantite());
            }

            int stockAvant = article.getStockActuel() != null ? article.getStockActuel() : 0;

            if (request.type() == TypeMovementEnum.SORTIE && stockAvant < request.quantite()) {
                throw new RuntimeException("Stock insuffisant pour cette sortie. Stock actuel : " + stockAvant);
            }

            article.setStockInitial(stockAvant);
            int stockApres = request.type() == TypeMovementEnum.ENTREE
                    ? stockAvant + request.quantite()
                    : stockAvant - request.quantite();

            article.setStockActuel(stockApres);

            StockMovement movement = mapper.toEntity(request);
            movement.setArticle(article);
            movement.setStockAvantOperation(stockAvant);
            movement.setStockApresOperation(stockApres);
            movement.setCreatedAt(LocalDateTime.now());

            if (movement.getDateOperation() == null) {
                movement.setDateOperation(LocalDate.now());
            }

            articleRepository.save(article);
            movement = movementRepository.save(movement);

            return mapper.toResponse(movement);


    }

    // 🟢 HISTORIQUE GLOBAL
    @Override
    public Page<StockMovementResponseDTO> getHistory(String reference, String designation, Pageable pageable) {
        return movementRepository.searchMovements(reference, designation, pageable)
                .map(mapper::toResponse); // Utilisation de toResponse au lieu de toDTO
    }

    // 🟢 HISTORIQUE PAR ARTICLE
    @Override
    public Page<StockMovementResponseDTO> getHistoryByArticleId(Long articleId, Pageable pageable) {
        if (!articleRepository.existsById(articleId)) {
            throw new RuntimeException("Article introuvable");
        }

        return movementRepository.findByArticleId(articleId, pageable)
                .map(mapper::toResponse);
    }
}