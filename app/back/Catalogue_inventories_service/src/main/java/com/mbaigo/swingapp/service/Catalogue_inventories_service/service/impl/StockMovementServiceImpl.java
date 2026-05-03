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

@Service @RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {
    private final StockMovementRepository movementRepository;
    private final ArticleRepository articleRepository;
    private final StockMovementMapper mapper;

    // 🔥 CRÉATION D’UN MOUVEMENT
    @Transactional
    public StockMovementResponseDTO createMovement(StockMovementRequestDTO dto) {

        // 1️⃣ Vérifier l'article
        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(() -> new RuntimeException("Article introuvable"));

        // 2️⃣ Validation de la quantité
        if (dto.quantite() == null || dto.quantite() <= 0) {
            throw new RuntimeException("Quantité invalide");
        }

        // 3️⃣ Validation du type de mouvement
        if (dto.type() == null) {
            throw new RuntimeException("Type de mouvement obligatoire");
        }

        // 4️⃣ Vérification stock suffisant pour SORTIE
        if (dto.type() == TypeMovementEnum.SORTIE &&
                article.getStockActuel() < dto.quantite()) {
            throw new RuntimeException("Stock insuffisant");
        }

        // 5️⃣ Création du mouvement
        StockMovement movement = mapper.toEntity(dto);
        movement.setArticle(article);

        // 🔥 Toujours initialiser createdAt ici si pas fait dans mapper
        movement.setCreatedAt(LocalDateTime.now());

        // 6️⃣ Calcul du nouveau stock
        int stockAvant = article.getStockActuel();
        int stockApres;

        switch (dto.type()) {
            case ENTREE -> stockApres = stockAvant + dto.quantite();
            case SORTIE -> stockApres = stockAvant - dto.quantite();
            default -> throw new RuntimeException("Type de mouvement invalide");
        }

        // 7️⃣ Mise à jour du stock
        article.setStockActuel(stockApres);

        // 🔥 BONUS (audit avancé recommandé)
        movement.setStockApresOperation(stockApres);

        // 8️⃣ Sauvegarde (ordre important)
        movementRepository.save(movement);
        articleRepository.save(article);

        return mapper.toDTO(movement);
    }

    // 🔥 HISTORIQUE
    public Page<StockMovementResponseDTO> getHistory(
            String reference,
            String designation,
            Pageable pageable
    ) {

        Page<StockMovement> movements =
                movementRepository.searchMovements(reference, designation, pageable);

        return movements.map(mapper::toDTO);
    }

    @Override
    public Page<StockMovementResponseDTO> getHistoryByArticleId(
            Long articleId,
            Pageable pageable
    ) {
        if (!articleRepository.existsById(articleId)) {
            throw new RuntimeException("Article introuvable");
        }

        return movementRepository
                .findByArticleId(articleId, pageable)
                .map(mapper::toDTO);
    }
}
