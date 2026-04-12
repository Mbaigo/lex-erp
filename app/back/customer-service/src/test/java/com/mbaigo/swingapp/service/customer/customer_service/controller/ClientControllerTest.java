package com.mbaigo.swingapp.service.customer.customer_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.ClientResponseDTO;
import com.mbaigo.swingapp.service.customer.customer_service.enums.Genre;
import com.mbaigo.swingapp.service.customer.customer_service.services.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClientController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive Spring Security pour ce test unitaire
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService; // On simule le service

    @Test
    void createClient_Returns201_WhenValid() throws Exception {
        // Arrange
        ClientRequestDTO request = new ClientRequestDTO("Dupont", "Jean", "+2376000000", null, Genre.HOMME, null);
        ClientResponseDTO response = new ClientResponseDTO(1L, "Dupont", "Jean", "+2376000000", null, Genre.HOMME, null, null);

        when(clientService.createClient(any(ClientRequestDTO.class))).thenReturn(response);

        // Act & Assert : On simule la requête HTTP
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Dupont"));
    }

    @Test
    void createClient_Returns400_WhenNomIsBlank() throws Exception {
        // Arrange : Nom vide (Viole le @NotBlank)
        ClientRequestDTO requestInvalid = new ClientRequestDTO("", "Jean", "+2376000000", null, Genre.HOMME, null);

        // Act & Assert
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalid)))
                .andExpect(status().isBadRequest())
                // Si ton GlobalExceptionHandler est bien configuré, tu peux tester sa structure !
                .andExpect(jsonPath("$.validationErrors.nom").exists());
    }
}
