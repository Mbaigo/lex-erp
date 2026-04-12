package com.mbaigo.swingapp.service.customer.customer_service.controller;

import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureResponseDTO;
import com.mbaigo.swingapp.service.customer.customer_service.services.FicheMesureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fiches-mesures")
@RequiredArgsConstructor
public class FicheMesureController {

    private final FicheMesureService ficheMesureService;

    // --- US : Créer une fiche ---
    @PostMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    @PreAuthorize("hasAnyRole('MANAGER','TAILOR')")
    public ResponseEntity<FicheMesureResponseDTO> createFicheMesure(@Valid @RequestBody FicheMesureRequestDTO requestDTO) {
        FicheMesureResponseDTO createdFiche = ficheMesureService.createFicheMesure(requestDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFiche.id())
                .toUri();

        return ResponseEntity.created(location).body(createdFiche);
    }

    // --- US : Récupérer une fiche précise ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<FicheMesureResponseDTO> getFicheById(@PathVariable Long id) {
        return ResponseEntity.ok(ficheMesureService.getFicheById(id));
    }

    // --- US : Consulter l'historique d'un client (Le plus récent en premier) ---
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<List<FicheMesureResponseDTO>> getFichesByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(ficheMesureService.getFichesByClientId(clientId));
    }

    // --- US : Mettre à jour une fiche ---
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<FicheMesureResponseDTO> updateFicheMesure(
            @PathVariable Long id,
            @Valid @RequestBody FicheMesureRequestDTO requestDTO) {
        return ResponseEntity.ok(ficheMesureService.updateFicheMesure(id, requestDTO));
    }
}
