package com.mbaigo.swingapp.service.customer.customer_service.controller;

import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientResponseDTO;
import com.mbaigo.swingapp.service.customer.customer_service.services.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // US : Récupérer un client par son ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id) {
        Optional<ClientResponseDTO> clientOpt = clientService.getClientById(id);

        return clientOpt
                .map(ResponseEntity::ok) // Si trouvé -> HTTP 200 avec le DTO en JSON
                .orElse(ResponseEntity.notFound().build()); // Si absent -> HTTP 404 Not Found
    }


    // Récupérer tous les clients
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<Page<ClientResponseDTO>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(clientService.getAllClients(page, size));
    }

    // US 1.2 : Recherche rapide par téléphone (ex: /api/v1/clients/search?telephone=+2376000000)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('MANAGER', 'TAILOR')")
    public ResponseEntity<Optional<ClientResponseDTO>> searchByTelephone(@RequestParam String telephone) {
        return ResponseEntity.ok(clientService.getClientByTelephone(telephone));
    }

    // US 1.1 : Création
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','TAILOR')")
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientRequestDTO requestDTO) {
        ClientResponseDTO createdClient = clientService.createClient(requestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdClient.id())
                .toUri();
        return ResponseEntity.created(location).body(createdClient);
    }

    // US 1.3 : Mise à jour
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO requestDTO) {
        return ResponseEntity.ok(clientService.updateClient(id, requestDTO));
    }
}