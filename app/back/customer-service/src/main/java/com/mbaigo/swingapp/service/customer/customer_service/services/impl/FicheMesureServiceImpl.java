package com.mbaigo.swingapp.service.customer.customer_service.services.impl;

import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureResponseDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.mappers.FicheMesureMapper;
import com.mbaigo.swingapp.service.customer.customer_service.models.Client;
import com.mbaigo.swingapp.service.customer.customer_service.models.FicheMesure;
import com.mbaigo.swingapp.service.customer.customer_service.repositories.ClientRepository;
import com.mbaigo.swingapp.service.customer.customer_service.repositories.FicheMesureRepository;
import com.mbaigo.swingapp.service.customer.customer_service.services.FicheMesureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FicheMesureServiceImpl implements FicheMesureService {

    private final FicheMesureRepository ficheMesureRepository;
    private final ClientRepository clientRepository;
    private final FicheMesureMapper ficheMesureMapper;

    @Override
    @Transactional
    public FicheMesureResponseDTO createFicheMesure(FicheMesureRequestDTO dto) {
        // 1. Vérifier que le client existe
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Impossible de créer la fiche : le client avec l'ID " + dto.clientId() + " n'existe pas."));

        // 2. Mapper et sauvegarder
        FicheMesure fiche = ficheMesureMapper.toEntity(dto, client);
        FicheMesure savedFiche = ficheMesureRepository.save(fiche);

        return ficheMesureMapper.toDto(savedFiche);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FicheMesureResponseDTO> getFichesByClientId(Long clientId) {
        // Vérification optionnelle mais propre pour renvoyer 404 plutôt qu'une liste vide si le client n'existe pas
        if (!clientRepository.existsById(clientId)) {
            throw new EntityNotFoundException("Client introuvable (ID: " + clientId + ")");
        }

        return ficheMesureRepository.findByClientIdOrderByDatePriseDesc(clientId).stream()
                .map(ficheMesureMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public FicheMesureResponseDTO updateFicheMesure(Long id, FicheMesureRequestDTO dto) {
        // 1. Récupérer la fiche existante
        FicheMesure ficheExistante = ficheMesureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fiche de mesure introuvable (ID: " + id + ")"));

        // 2. Règle métier : Interdire le transfert d'une fiche à un autre client
        if (!ficheExistante.getClient().getId().equals(dto.clientId())) {
            throw new IllegalArgumentException("Opération refusée : Impossible de transférer une fiche de mesure vers un autre client.");
        }

        // 3. Mettre à jour les données (y compris le JSONB)
        ficheMesureMapper.updateEntityFromDto(ficheExistante, dto);

        // 4. Sauvegarder et retourner
        return ficheMesureMapper.toDto(ficheMesureRepository.save(ficheExistante));
    }

    @Override
    public Page<FicheMesureResponseDTO> getAllFiches(int page, int size) {
        // On crée la requête de pagination (Page 0 par défaut, triée par nom de A à Z)
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePrise").ascending());
        return ficheMesureRepository.findAll(pageable).map(ficheMesureMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public FicheMesureResponseDTO getFicheById(Long id) {
        return ficheMesureRepository.findById(id)
                .map(ficheMesureMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Fiche de mesure introuvable (ID: " + id + ")"));
    }
}
