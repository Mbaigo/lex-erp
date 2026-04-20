package com.mbaigo.swingapp.service.customer.customer_service.serviceImpl;

import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientResponseDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.mappers.ClientMapper;
import com.mbaigo.swingapp.service.customer.customer_service.enums.Genre;
import com.mbaigo.swingapp.service.customer.customer_service.models.Client;
import com.mbaigo.swingapp.service.customer.customer_service.repositories.ClientRepository;
import com.mbaigo.swingapp.service.customer.customer_service.services.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientRequestDTO validRequest;
    private Client savedClient;
    private ClientResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validRequest = new ClientRequestDTO("Dupont", "Jean", "+237 6 00 00", "jean@mail.com", Genre.HOMME, null);
        savedClient = Client.builder().id(1L).nom("Dupont").telephone("+23760000").build();
        responseDTO = new ClientResponseDTO(1L, "Dupont", "Jean", "+23760000", "jean@mail.com", Genre.HOMME, null, null);
    }

    @Test
    void createClient_Success() {
        // Arrange : On configure le comportement des fausses dépendances (Mocks)
        when(clientRepository.existsByTelephone("+23760000")).thenReturn(false);
        when(clientMapper.toEntity(any(ClientRequestDTO.class), anyString())).thenReturn(savedClient);
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(clientMapper.toDto(savedClient)).thenReturn(responseDTO);

        // Act : On appelle la vraie méthode
        ClientResponseDTO result = clientService.createClient(validRequest);

        // Assert : On vérifie que le résultat est correct
        assertNotNull(result);
        assertEquals("Dupont", result.nom());

        // On vérifie que le repository a bien été appelé une fois pour sauvegarder
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void createClient_ThrowsException_WhenTelephoneExists() {
        // Arrange : Le téléphone existe déjà en base
        when(clientRepository.existsByTelephone("+23760000")).thenReturn(true);

        // Act & Assert : On s'attend à une erreur métier
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(validRequest);
        });

        assertTrue(exception.getMessage().contains("existe déjà"));
        // On vérifie que le save n'a JAMAIS été appelé (sécurité)
        verify(clientRepository, never()).save(any());
    }
}
