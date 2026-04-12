package com.mbaigo.swingapp.service.order_service.controller;

import com.mbaigo.swingapp.service.order_service.dto.CommandeRequest;
import com.mbaigo.swingapp.service.order_service.dto.CommandeResponse;
import com.mbaigo.swingapp.service.order_service.entities.Commande;
import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;
import com.mbaigo.swingapp.service.order_service.services.CommandeService;
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
@RequestMapping("/api/v1/commandes")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Endpoints pour la gestion des commandes et des confections")
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "US 5.1/5.2/5.3 - Créer une nouvelle commande",
            description = "Instancie une commande, applique les substitutions de tissus et fige les prix (snapshot).")
    public ResponseEntity<CommandeResponse> createCommande(@Valid @RequestBody CommandeRequest request) {
        CommandeResponse response = commandeService.createCommande(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Récupérer une commande par son ID",
            description = "Affiche le détail de la commande avec ses lignes de matériaux et le prix total calculé.")
    public ResponseEntity<CommandeResponse> getCommandeById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getCommandeById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "Lister toutes les commandes",
            description = "Retourne l'ensemble des commandes de l'atelier.")
    public ResponseEntity<List<CommandeResponse>> getAllCommandes() {
        return ResponseEntity.ok(commandeService.getAllCommandes());
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @Operation(summary = "US 6.1 - Mettre à jour le statut", description = "Fait avancer la commande dans le workflow de production.")
    public ResponseEntity<CommandeResponse> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutCommande nouveauStatut) {
        return ResponseEntity.ok(commandeService.updateStatut(id, nouveauStatut));
    }

    @PostMapping("/{id}/annuler")
    @Operation(summary = "US 6.2 - Annuler une commande et Rollback", description = "Annule la commande et contacte le catalogue pour remettre les tissus en stock.")
    public ResponseEntity<CommandeResponse> annulerCommande(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.annulerCommande(id));
    }

    // Endpoint : GET /api/v1/commandes/rendez-vous?filtre=semaine
    @GetMapping("/rendez-vous")
    public ResponseEntity<List<CommandeResponse>> getPlanning(@RequestParam(defaultValue = "journalier") String filtre) {
        List<CommandeResponse> planning = commandeService.getRendezVousParPeriode(filtre);
        return ResponseEntity.ok(planning);
    }
}
