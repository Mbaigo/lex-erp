package com.mbaigo.swingapp.service.customer.customer_service.services;

import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientService {
    // US 1.1
    ClientResponseDTO createClient(ClientRequestDTO requestDTO);
    // US 1.2
    ClientResponseDTO getClientByTelephone(String telephone);
    // US 1.3
    ClientResponseDTO updateClient(Long id, ClientRequestDTO requestDTO);

    Page<ClientResponseDTO> getAllClients(int page, int size);
}
