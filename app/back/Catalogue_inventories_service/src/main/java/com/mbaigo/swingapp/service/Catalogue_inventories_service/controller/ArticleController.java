package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.RestockItemRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PatchMapping("/{reference}/stock")
    @PreAuthorize("hasAnyRole('MANAGER')")
    @Operation(summary = "US 3.3 - Mouvement de stock (Débit/Crédit)", description = "Permet de décrémenter (isDebit=true) ou incrémenter (isDebit=false) le stock d'un article de manière transactionnelle.")
    public ResponseEntity<ArticleResponse> updateStock(
            @PathVariable String reference,
            @RequestParam Double quantite,
            @RequestParam boolean isDebit) {
        ArticleResponse response = articleService.updateStock(reference, quantite, isDebit);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Lister tous les articles", description = "Récupère l'inventaire complet de l'atelier.")
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        List<ArticleResponse> responses = articleService.getAllArticles();
        return ResponseEntity.ok(responses);
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

    @PostMapping("/stock/restock-batch")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "US 6.2 - Recréditer les stocks en masse", description = "Utilisé par le order-service lors de l'annulation d'une commande.")
    public ResponseEntity<Void> restockBatch(@Valid @RequestBody List<RestockItemRequest> requests) {
        articleService.restockBatch(requests);
        return ResponseEntity.ok().build();
    }

}
