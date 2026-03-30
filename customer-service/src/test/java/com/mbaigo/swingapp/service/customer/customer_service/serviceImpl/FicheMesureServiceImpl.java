package com.mbaigo.swingapp.service.customer.customer_service.serviceImpl;
import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.repositories.ClientRepository;
import com.mbaigo.swingapp.service.customer.customer_service.repositories.FicheMesureRepository;
import com.mbaigo.swingapp.service.customer.customer_service.services.impl.FicheMesureServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FicheMesureServiceImplTest {

    @Mock
    private FicheMesureRepository ficheMesureRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private FicheMesureServiceImpl ficheMesureService;

    @Test
    void createFicheMesure_ThrowsNotFound_WhenClientDoesNotExist() {
        // Arrange
        FicheMesureRequestDTO request = new FicheMesureRequestDTO(99L, "Costume", LocalDate.now(), Map.of("taille", 40.0));

        // On simule que le client ID 99 n'existe pas
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ficheMesureService.createFicheMesure(request);
        });

        assertTrue(exception.getMessage().contains("n'existe pas"));
        verify(ficheMesureRepository, never()).save(any());
    }
}
