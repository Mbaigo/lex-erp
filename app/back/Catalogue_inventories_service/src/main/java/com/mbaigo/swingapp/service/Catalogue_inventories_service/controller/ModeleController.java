package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ModeleService;
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
@RequestMapping("/api/v1/modeles")
@RequiredArgsConstructor
@Tag(name = "Modèles & Nomenclature", description = "API de gestion des recettes de fabrication (BOM) et du calcul des coûts de base.")
public class ModeleController {

    private final ModeleService modeleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "US 4.1 & 4.2 - Créer un modèle", description = "Crée un nouveau modèle de vêtement avec sa liste complète de matériaux nécessaires.")
    public ResponseEntity<ModeleResponse> createModele(@Valid @RequestBody ModeleRequest request) {
        ModeleResponse response = modeleService.createModele(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Lister tous les modèles", description = "Récupère le catalogue complet des modèles avec leurs coûts précalculés.")
    public ResponseEntity<List<ModeleResponse>> getAllModeles() {
        List<ModeleResponse> responses = modeleService.getAllModeles();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Détails d'un modèle", description = "Récupère un modèle spécifique et sa nomenclature complète.")
    public ResponseEntity<ModeleResponse> getModeleById(@PathVariable Long id) {
        ModeleResponse response = modeleService.getModeleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Modifier un modèle", description = "Met à jour un modèle et remplace entièrement son ancienne nomenclature par la nouvelle.")
    public ResponseEntity<ModeleResponse> updateModele(@PathVariable Long id, @Valid @RequestBody ModeleRequest request) {
        ModeleResponse response = modeleService.updateModele(id, request);
        return ResponseEntity.ok(response);
    }
}
