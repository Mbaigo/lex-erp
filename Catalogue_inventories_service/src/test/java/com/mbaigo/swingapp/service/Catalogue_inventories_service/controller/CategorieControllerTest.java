package com.mbaigo.swingapp.service.Catalogue_inventories_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.CategorieService;
import org.junit.jupiter.api.DisplayName;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategorieController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive temporairement Keycloak pour ce TU
class CategorieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets en JSON

    @MockitoBean // Attention : @MockBean (Spring) et pas @Mock (Mockito pur) pour le contexte Web
    private CategorieService categorieService;

    @Test
    @DisplayName("Doit retourner un status 201 CREATED lors de la création d'une catégorie")
    void createCategorie_shouldReturn201() throws Exception {
        // Arrange
        CategorieRequest request = new CategorieRequest("TIS", "Tissus", "Description");
        CategorieResponse response = new CategorieResponse(1L, "TIS", "Tissus", "Description");

        when(categorieService.createCategorie(any(CategorieRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.code").value("TIS"));
    }

    @Test
    @DisplayName("Doit retourner un status 400 BAD REQUEST si le code est vide")
    void createCategorie_shouldReturn400_whenValidationFails() throws Exception {
        // Arrange : Requête invalide (code null)
        CategorieRequest invalidRequest = new CategorieRequest(null, "Tissus", "");

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
