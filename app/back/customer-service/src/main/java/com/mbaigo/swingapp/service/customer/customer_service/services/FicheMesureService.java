package com.mbaigo.swingapp.service.customer.customer_service.services;

import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureResponseDTO;

import java.util.List;

public interface FicheMesureService {
    FicheMesureResponseDTO createFicheMesure(FicheMesureRequestDTO requestDTO);
    List<FicheMesureResponseDTO> getFichesByClientId(Long clientId);
    FicheMesureResponseDTO getFicheById(Long id);
    // ... (autres méthodes)
    FicheMesureResponseDTO updateFicheMesure(Long id, FicheMesureRequestDTO requestDTO);
}
