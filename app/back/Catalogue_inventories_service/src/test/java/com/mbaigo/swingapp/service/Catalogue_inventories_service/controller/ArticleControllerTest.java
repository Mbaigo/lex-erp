package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.StockMovementRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArticleControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ArticleService articleService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("PATCH /api/v1/articles/{ref}/stock - Succès")
    void updateStock_shouldReturnOk() throws Exception {
        // GIVEN
        String reference = "REF-01";
        // Création du DTO via le Record
        StockMovementRequest request = new StockMovementRequest(5.0, true, "Vente client");

        // Simulation du retour du service (ArticleResponse factice)
        ArticleResponse mockResponse = ArticleResponse.builder()
                .reference(reference)
                .quantiteEnStock(15.0) // On imagine que c'était 20 avant le débit de 5
                .designation("Tissu Wax")
                .build();

        // On configure le mock du service
        when(articleService.updateStock(eq(reference), any(StockMovementRequest.class)))
                .thenReturn(mockResponse);

        // WHEN & THEN
        mockMvc.perform(patch("/api/v1/articles/{reference}/stock", reference)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Envoi du JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value(reference))
                .andExpect(jsonPath("$.quantiteEnStock").value(15.0));

        // Vérification stricte de l'appel au service
        verify(articleService).updateStock(eq(reference), eq(request));
    }

    @Test
    @DisplayName("PATCH /stock - Doit retourner 400 Bad Request si la quantité est négative")
    void updateStock_shouldReturnBadRequest() throws Exception {
        // GIVEN : Quantité négative (-5.0)
        StockMovementRequest invalidRequest = new StockMovementRequest(-5.0, true, "");

        // WHEN & THEN
        mockMvc.perform(patch("/api/v1/articles/REF-01/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Doit échouer grâce à @Valid

        // On vérifie que le service n'a JAMAIS été appelé
        verifyNoInteractions(articleService);
    }
}

