package com.mbaigo.swingapp.service.order_service.services;

import com.mbaigo.swingapp.service.order_service.clientDto.CatalogArticleDTO;
import com.mbaigo.swingapp.service.order_service.clientDto.CatalogModeleDTO;
import com.mbaigo.swingapp.service.order_service.clientService.CatalogServiceClient;
import com.mbaigo.swingapp.service.order_service.dto.CommandeRequest;
import com.mbaigo.swingapp.service.order_service.dto.CommandeResponse;
import com.mbaigo.swingapp.service.order_service.dto.LigneMateriauCommandeRequest;
import com.mbaigo.swingapp.service.order_service.entities.Commande;
import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;
import com.mbaigo.swingapp.service.order_service.mappers.CommandeMapper;
import com.mbaigo.swingapp.service.order_service.repositories.CommandeRepository;
import com.mbaigo.swingapp.service.order_service.services.impl.CommandeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceImplTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private CatalogServiceClient catalogClient;

    @Mock
    private CommandeMapper commandeMapper;

    @InjectMocks
    private CommandeServiceImpl commandeService;

    @Captor
    private ArgumentCaptor<Commande> commandeCaptor; // Permet de capturer l'entité juste avant le save()

    @Test
    @DisplayName("Doit créer une commande, faire le snapshot des prix et calculer le total (US 5.1/5.2/5.3)")
    void createCommande_shouldSuccess_andCalculateTotal() {
        // 1. Arrange : Préparation des données entrantes
        Long clientId = 100L;
        Long modeleId = 10L;
        LigneMateriauCommandeRequest ligne1 = new LigneMateriauCommandeRequest(1L, 2.0); // 2 mètres du tissu 1
        LigneMateriauCommandeRequest ligne2 = new LigneMateriauCommandeRequest(2L, 5.0); // 5 boutons 2
        CommandeRequest request = new CommandeRequest(clientId, modeleId, List.of(ligne1, ligne2));

        // 2. Arrange : Simulation du Catalogue (Le Client Feign)
        CatalogModeleDTO modeleMock = new CatalogModeleDTO(modeleId, "MOD-1", "Robe", new BigDecimal("50.00"), List.of());
        CatalogArticleDTO tissuMock = new CatalogArticleDTO(1L, "TIS-1", "Soie", new BigDecimal("10.00")); // 10€/m
        CatalogArticleDTO boutonMock = new CatalogArticleDTO(2L, "BTN-1", "Bouton Or", new BigDecimal("2.00")); // 2€/pièce

        when(catalogClient.getModeleById(modeleId)).thenReturn(modeleMock);
        when(catalogClient.getArticlesInBatch(List.of(1L, 2L))).thenReturn(List.of(tissuMock, boutonMock));

        // 3. Arrange : Simulation de la BDD et du Mapper
        Commande savedCommande = new Commande();
        CommandeResponse expectedResponse = new CommandeResponse(1L, "CMD-TEST", null, clientId, modeleId, new BigDecimal("50.00"), new BigDecimal("80.00"), StatutCommande.CREEE, List.of());

        when(commandeRepository.save(any(Commande.class))).thenReturn(savedCommande);
        when(commandeMapper.toResponse(savedCommande)).thenReturn(expectedResponse);

        // 4. Act : Exécution
        CommandeResponse result = commandeService.createCommande(request);

        // 5. Assert : Vérifications
        verify(commandeRepository).save(commandeCaptor.capture()); // On capture l'entité qui a été générée par le service
        Commande capturedCommande = commandeCaptor.getValue();

        // Vérification du calcul (US 5.3) : Main d'oeuvre (50) + (2m * 10€) + (5 pièces * 2€) = 50 + 20 + 10 = 80.00
        assertThat(capturedCommande.getPrixTotal()).isEqualByComparingTo(new BigDecimal("80.00"));
        assertThat(capturedCommande.getMateriaux()).hasSize(2);

        // On vérifie que la réponse retournée est bien celle mappée
        assertThat(result.prixTotal()).isEqualTo(new BigDecimal("80.00"));
    }

    @Test
    @DisplayName("Doit lever une exception si un article demandé n'existe pas dans le catalogue (US 5.2)")
    void createCommande_shouldThrowException_whenArticleNotFoundInBatch() {
        // Arrange
        CommandeRequest request = new CommandeRequest(100L, 10L, List.of(new LigneMateriauCommandeRequest(99L, 2.0)));
        CatalogModeleDTO modeleMock = new CatalogModeleDTO(10L, "MOD-1", "Robe", BigDecimal.TEN, List.of());

        when(catalogClient.getModeleById(10L)).thenReturn(modeleMock);
        // Le catalogue renvoie une liste vide car l'ID 99 n'existe pas
        when(catalogClient.getArticlesInBatch(List.of(99L))).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> commandeService.createCommande(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("n'existe pas dans le catalogue");

        verify(commandeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit retourner une commande par son ID")
    void getCommandeById_shouldReturnCommande() {
        Long id = 1L;
        Commande commandeMock = new Commande();
        CommandeResponse responseMock = new CommandeResponse(id, "CMD-1", null, 1L, 1L, null, null, null, null);

        when(commandeRepository.findById(id)).thenReturn(Optional.of(commandeMock));
        when(commandeMapper.toResponse(commandeMock)).thenReturn(responseMock);

        CommandeResponse result = commandeService.getCommandeById(id);

        assertThat(result.id()).isEqualTo(id);
    }
}
