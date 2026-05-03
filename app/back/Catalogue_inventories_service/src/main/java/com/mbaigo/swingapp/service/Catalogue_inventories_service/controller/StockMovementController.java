package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementRequestDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.StockMovementResponseDTO;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    // 🔥 Créer mouvement (entrée/sortie)
    @PostMapping
    public ResponseEntity<StockMovementResponseDTO> createMovement(
            @RequestBody @Valid StockMovementRequestDTO dto
    ) {
        return ResponseEntity.ok(stockMovementService.createMovement(dto));
    }

    // 🔥 Recherche globale (par reference ou designation)
    @GetMapping
    public ResponseEntity<Page<StockMovementResponseDTO>> getHistory(
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String designation,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                stockMovementService.getHistory(reference, designation, pageable)
        );
    }
}
