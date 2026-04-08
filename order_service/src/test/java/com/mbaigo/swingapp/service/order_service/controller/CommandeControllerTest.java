package com.mbaigo.swingapp.service.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbaigo.swingapp.service.order_service.dto.CommandeRequest;
import com.mbaigo.swingapp.service.order_service.dto.CommandeResponse;
import com.mbaigo.swingapp.service.order_service.dto.LigneMateriauCommandeRequest;
import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;
import com.mbaigo.swingapp.service.order_service.services.CommandeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommandeController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive temporairement Spring Security/Keycloak
class CommandeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommandeService commandeService;

    @Test
    @DisplayName("POST /commandes - Doit créer une commande et retourner 201 CREATED")
    void createCommande_shouldReturn201() throws Exception {
        // Arrange
        LigneMateriauCommandeRequest ligneReq = new LigneMateriauCommandeRequest(1L, 3.5);
        CommandeRequest request = new CommandeRequest(100L, 10L, List.of(ligneReq));

        CommandeResponse response = new CommandeResponse(
                1L, "CMD-A1B2C3D4", null, 100L, 10L,
                new BigDecimal("50.00"), new BigDecimal("85.00"),
                StatutCommande.CREEE, List.of()
        );

        when(commandeService.createCommande(any(CommandeRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reference").value("CMD-A1B2C3D4"))
                .andExpect(jsonPath("$.prixTotal").value(85.00));
    }

    @Test
    @DisplayName("POST /commandes - Doit échouer avec 400 BAD REQUEST si les matériaux sont vides")
    void createCommande_shouldReturn400_whenMateriauxEmpty() throws Exception {
        // Arrange : Requête invalide (Liste vide violant l'annotation @NotEmpty du DTO)
        CommandeRequest invalidRequest = new CommandeRequest(100L, 10L, List.of());

        // Act & Assert
        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /commandes/{id} - Doit retourner la commande et 200 OK")
    void getCommandeById_shouldReturn200() throws Exception {
        // Arrange
        Long id = 1L;
        CommandeResponse response = new CommandeResponse(
                id, "CMD-TEST", null, 100L, 10L,
                BigDecimal.TEN, BigDecimal.TEN,
                StatutCommande.CREEE, List.of()
        );

        when(commandeService.getCommandeById(id)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/commandes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.reference").value("CMD-TEST"));
    }

    @Test
    @DisplayName("GET /commandes - Doit retourner la liste des commandes et 200 OK")
    void getAllCommandes_shouldReturn200() throws Exception {
        // Arrange
        CommandeResponse r1 = new CommandeResponse(1L, "CMD-1", null, 1L, 1L, null, null, null, null);
        CommandeResponse r2 = new CommandeResponse(2L, "CMD-2", null, 1L, 1L, null, null, null, null);

        when(commandeService.getAllCommandes()).thenReturn(List.of(r1, r2));

        // Act & Assert
        mockMvc.perform(get("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("PATCH /commandes/{id}/statut - Doit retourner 200 OK")
    void updateStatut_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/commandes/1/statut")
                        .param("nouveauStatut", "EN_CONFECTION"))
                .andExpect(status().isOk());

        verify(commandeService).updateStatut(1L, StatutCommande.EN_CONFECTION);
    }

    @Test
    @DisplayName("POST /commandes/{id}/annuler - Doit retourner 200 OK")
    void annulerCommande_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/commandes/1/annuler"))
                .andExpect(status().isOk());

        verify(commandeService).annulerCommande(1L);
    }
}
