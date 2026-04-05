package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.LigneNomenclatureRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.ModeleService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModeleController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive temporairement Keycloak
class ModeleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModeleService modeleService;

    @Test
    @DisplayName("POST /modeles - Doit créer un modèle et retourner 201 CREATED")
    void createModele_shouldReturn201() throws Exception {
        // Arrange
        LigneNomenclatureRequest ligneReq = new LigneNomenclatureRequest(1L, 3.5);
        ModeleRequest request = new ModeleRequest("ROBE-01", "Robe Été", "Belle robe", new BigDecimal("50.00"), List.of(ligneReq));

        ModeleResponse response = new ModeleResponse(1L, "ROBE-01", "Robe Été", "Belle robe", new BigDecimal("50.00"), new BigDecimal("85.00"), List.of());

        when(modeleService.createModele(any(ModeleRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/modeles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reference").value("ROBE-01"))
                .andExpect(jsonPath("$.coutDeBase").value(85.00));
    }

    @Test
    @DisplayName("GET /modeles - Doit lister tous les modèles et retourner 200 OK")
    void getAllModeles_shouldReturn200() throws Exception {
        // Arrange
        ModeleResponse m1 = new ModeleResponse(1L, "MOD-1", "Nom 1", null, BigDecimal.TEN, BigDecimal.TEN, List.of());
        ModeleResponse m2 = new ModeleResponse(2L, "MOD-2", "Nom 2", null, BigDecimal.TEN, BigDecimal.TEN, List.of());

        when(modeleService.getAllModeles()).thenReturn(List.of(m1, m2));

        // Act & Assert
        mockMvc.perform(get("/api/modeles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].reference").value("MOD-1"))
                .andExpect(jsonPath("$[1].reference").value("MOD-2"));
    }

    @Test
    @DisplayName("GET /modeles/{id} - Doit retourner un modèle spécifique et 200 OK")
    void getModeleById_shouldReturn200() throws Exception {
        // Arrange
        Long modeleId = 1L;
        ModeleResponse response = new ModeleResponse(modeleId, "ROBE-01", "Robe Été", null, BigDecimal.TEN, BigDecimal.TEN, List.of());

        when(modeleService.getModeleById(modeleId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/modeles/{id}", modeleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(modeleId))
                .andExpect(jsonPath("$.reference").value("ROBE-01"));
    }

    @Test
    @DisplayName("PUT /modeles/{id} - Doit modifier un modèle et retourner 200 OK")
    void updateModele_shouldReturn200() throws Exception {
        // Arrange
        Long modeleId = 1L;
        // J'ajoute une ligne valide pour passer la validation @NotEmpty
        LigneNomenclatureRequest ligneReq = new LigneNomenclatureRequest(2L, 1.5);
        ModeleRequest request = new ModeleRequest("MOD-UPDATE", "Nom Modifié", "Desc", new BigDecimal("60.00"), List.of(ligneReq));

        ModeleResponse response = new ModeleResponse(modeleId, "MOD-UPDATE", "Nom Modifié", "Desc", new BigDecimal("60.00"), new BigDecimal("90.00"), List.of());

        when(modeleService.updateModele(eq(modeleId), any(ModeleRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/modeles/{id}", modeleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("MOD-UPDATE"))
                .andExpect(jsonPath("$.coutMainOeuvre").value(60.00));
    }

    @Test
    @DisplayName("POST /modeles - Doit échouer avec 400 BAD REQUEST si la nomenclature est vide")
    void createModele_shouldReturn400_whenNomenclatureIsEmpty() throws Exception {
        // Arrange : On envoie une liste vide, ce qui viole l'annotation @NotEmpty de ModeleRequest
        ModeleRequest invalidRequest = new ModeleRequest("ROBE-01", "Robe", "Desc", BigDecimal.TEN, List.of());

        // Act & Assert
        mockMvc.perform(post("/api/modeles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Spring lève une MethodArgumentNotValidException
    }
}
