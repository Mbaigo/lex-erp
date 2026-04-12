package com.mbaigo.swingapp.service.customer.customer_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureRequestDTO;
import com.mbaigo.swingapp.service.customer.customer_service.dto.FicheMesureResponseDTO;
import com.mbaigo.swingapp.service.customer.customer_service.services.FicheMesureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // L'annotation moderne
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FicheMesureController.class)
@AutoConfigureMockMvc(addFilters = false) // On désactive Keycloak pour isoler le test du contrôleur
class FicheMesureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // L'outil de Spring qui transforme les Objets en JSON

    @MockitoBean
    private FicheMesureService ficheMesureService; // On simule le service avec le nouveau standard

    @Test
    void createFicheMesure_Returns201_WhenValid() throws Exception {
        // Arrange : On prépare les fausses données avec notre Map JSONB
        Map<String, Double> mesures = Map.of(
                "tour_poitrine", 105.5,
                "tour_taille", 88.0
        );

        FicheMesureRequestDTO request = new FicheMesureRequestDTO(
                1L, "Costume 3 pièces", LocalDate.of(2026, 3, 26), mesures
        );

        // On simule la réponse (j'inclus tous les champs de ton Record actuel)
        FicheMesureResponseDTO response = new FicheMesureResponseDTO(
                1L, 1L, "Costume 3 pièces",
                LocalDate.now(), mesures
        );

        when(ficheMesureService.createFicheMesure(any(FicheMesureRequestDTO.class))).thenReturn(response);

        // Act & Assert : On simule la requête HTTP POST
        mockMvc.perform(post("/api/v1/fiches-mesures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // On attend un code 201
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomProjet").value("Costume 3 pièces"))
                // On vérifie que Jackson a bien compris notre JSONB !
                .andExpect(jsonPath("$.valeurs.tour_poitrine").value(105.5))
                .andExpect(jsonPath("$.valeurs.tour_taille").value(88.0));
    }

    @Test
    void getFichesByClientId_Returns200_WithList() throws Exception {
        // Arrange
        Map<String, Double> mesures = Map.of("longueur_bras", 65.0);

        FicheMesureResponseDTO response = new FicheMesureResponseDTO(
                1L, 1L, "Ourlet Pantalon",
                LocalDate.now(), mesures
        );

        when(ficheMesureService.getFichesByClientId(1L)).thenReturn(List.of(response));

        // Act & Assert : On simule la requête HTTP GET
        mockMvc.perform(get("/api/v1/fiches-mesures/client/{clientId}", 1L))
                .andExpect(status().isOk()) // On attend un code 200
                .andExpect(jsonPath("$[0].id").value(1)) // $[0] car c'est le premier élément du tableau renvoyé
                .andExpect(jsonPath("$[0].nomProjet").value("Ourlet Pantalon"))
                .andExpect(jsonPath("$[0].valeurs.longueur_bras").value(65.0));
    }

    @Test
    void createFicheMesure_Returns400_WhenValeursAreNull() throws Exception {
        // Arrange : On crée une requête invalide (sans les mesures obligatoires)
        FicheMesureRequestDTO invalidRequest = new FicheMesureRequestDTO(
                1L, "Costume", LocalDate.now(), null // 🔴 Map null, viole le @NotNull
        );

        // Act & Assert : Le @Valid du contrôleur doit bloquer la requête avant même d'atteindre le service
        mockMvc.perform(post("/api/v1/fiches-mesures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // On attend un code 400
                .andExpect(jsonPath("$.validationErrors.valeurs").exists()); // On s'assure que le message d'erreur pointe bien sur "valeurs"
    }
}
