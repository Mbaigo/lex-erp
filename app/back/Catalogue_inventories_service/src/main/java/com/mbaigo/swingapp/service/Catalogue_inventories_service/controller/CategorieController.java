package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.CategorieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories", description = "API de gestion des catégories d'articles (Tissus, Fils, Prêt-à-porter...)")
public class CategorieController {

    private final CategorieService categorieService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','TAILOR')")
    @Operation(summary = "Créer une nouvelle catégorie", description = "Ajoute une nouvelle catégorie dans le catalogue de l'atelier.")
    public ResponseEntity<CategorieResponse> createCategorie(@Valid @RequestBody CategorieRequest request) {
        CategorieResponse response = categorieService.createCategorie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER')")
    @Operation(summary = "Lister toutes les catégories", description = "Récupère la liste complète des catégories disponibles.")
    public ResponseEntity<Page<CategorieResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categorieService.getAllCategories(page,size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Récupérer une catégorie", description = "Cherche une catégorie spécifique grâce à son ID.")
    public ResponseEntity<CategorieResponse> getCategorieById(@PathVariable Long id) {
        CategorieResponse response = categorieService.getCategorieById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Modifier une catégorie", description = "Met à jour les informations d'une catégorie existante.")
    public ResponseEntity<CategorieResponse> updateCategorie(@PathVariable Long id, @Valid @RequestBody CategorieRequest request) {
        CategorieResponse response = categorieService.updateCategorie(id, request);
        return ResponseEntity.ok(response);
    }
}
