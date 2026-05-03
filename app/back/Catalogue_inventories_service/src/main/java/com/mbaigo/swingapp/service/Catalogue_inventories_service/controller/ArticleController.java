package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementResponseDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.RestockItemRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.StockMovementRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ArticleService;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Tag(name = "Articles & Stock", description = "API de gestion de l'inventaire des matières premières et produits finis")
public class ArticleController {

    private final ArticleService articleService;
    private final StockMovementService stockMovementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER')")
    @Operation(summary = "US 3.1 - Ajouter un article", description = "Crée un nouvel article dans le catalogue et initialise son stock.")
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest request) {
        ArticleResponse response = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "US 3.2 - Tableau de bord des alertes", description = "Récupère la liste de tous les articles dont le stock est inférieur ou égal au seuil d'alerte.")
    public ResponseEntity<List<ArticleResponse>> getArticlesEnAlerte() {
        List<ArticleResponse> responses = articleService.getArticlesEnAlerte();
        return ResponseEntity.ok(responses);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Lister tous les articles", description = "Récupère l'inventaire complet de l'atelier.")
    public ResponseEntity<Page<ArticleResponse>> getAllArticles(
            Pageable pageable){
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Détails d'un article", description = "Récupère un article spécifique via son identifiant unique.")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id) {
        ArticleResponse response = articleService.getArticleById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "US 5.1/5.3 Optimisée - Récupérer un lot d'articles", description = "Permet de récupérer plusieurs articles en une seule requête pour éviter le problème N+1 réseau.")
    public ResponseEntity<List<ArticleResponse>> getArticlesByIds(@RequestBody List<Long> ids) {
        List<ArticleResponse> responses = articleService.getArticlesByIds(ids);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/byCategory")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByCategorie(
            @RequestParam(required = false) Long categorieId,
            Pageable pageable
    ) {
        if (categorieId != null) {
            return ResponseEntity.ok(
                    articleService.getArticlesByCategorie(categorieId, pageable)
            );
        }

        // fallback : tous les articles
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }

    @GetMapping("/api/v1/articles/{id}/stock-history")
    public ResponseEntity<Page<StockMovementResponseDTO>> getHistoryByArticleId(
            @PathVariable Long id,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                stockMovementService.getHistoryByArticleId(id, pageable)
        );
    }


}
