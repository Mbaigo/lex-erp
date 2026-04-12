package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ModeleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.LigneNomenclature;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Modele;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.ModeleMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ArticleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ModeleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl.ModeleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ModeleServiceImplTest {

    @Mock
    private ModeleRepository modeleRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ModeleMapper modeleMapper;

    @InjectMocks
    private ModeleServiceImpl modeleService;

    // ==========================================
    // 1. TESTS DE CRÉATION (CREATE)
    // ==========================================

    @Test
    @DisplayName("Doit créer un modèle avec succès")
    void createModele_shouldSuccess() {
        // Arrange
        ModeleRequest request = new ModeleRequest("ROBE-01", "Robe", "Desc", BigDecimal.TEN, List.of());
        Modele entity = Modele.builder().reference("ROBE-01").lignesNomenclature(new ArrayList<>()).build();

        // On simule une ligne de nomenclature attachée au modèle
        Article fauxArticleReq = Article.builder().id(99L).build();
        LigneNomenclature ligne = LigneNomenclature.builder().article(fauxArticleReq).build();
        entity.getLignesNomenclature().add(ligne);

        Article articleEnBase = Article.builder().id(99L).reference("TIS-01").build();
        ModeleResponse expectedResponse = new ModeleResponse(1L, "ROBE-01", "Robe", "Desc", BigDecimal.TEN, BigDecimal.TEN, List.of());

        when(modeleRepository.existsByReference("ROBE-01")).thenReturn(false);
        when(modeleMapper.toEntity(request)).thenReturn(entity);
        when(articleRepository.findById(99L)).thenReturn(Optional.of(articleEnBase));
        when(modeleRepository.save(entity)).thenReturn(entity);
        when(modeleMapper.toResponse(entity)).thenReturn(expectedResponse);

        // Act
        ModeleResponse actualResponse = modeleService.createModele(request);

        // Assert
        assertThat(actualResponse.reference()).isEqualTo("ROBE-01");
        assertThat(ligne.getModele()).isEqualTo(entity); // Vérifie le lien bidirectionnel
        assertThat(ligne.getArticle()).isEqualTo(articleEnBase); // Vérifie que le vrai article a été chargé
        verify(modeleRepository).save(entity);
    }

    @Test
    @DisplayName("Doit lever une exception si la référence existe déjà à la création")
    void createModele_shouldThrowException_whenRefExists() {
        ModeleRequest request = new ModeleRequest("ROBE-01", "Robe", "", BigDecimal.TEN, List.of());
        when(modeleRepository.existsByReference("ROBE-01")).thenReturn(true);

        assertThatThrownBy(() -> modeleService.createModele(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existe déjà");

        verify(modeleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit lever une exception si un article de la nomenclature n'existe pas")
    void createModele_shouldThrowException_whenArticleNotFound() {
        ModeleRequest request = new ModeleRequest("ROBE-01", "Robe", "", BigDecimal.TEN, List.of());
        Modele entity = Modele.builder().reference("ROBE-01").lignesNomenclature(new ArrayList<>()).build();

        // Article ID 99L n'existe pas en BDD
        entity.getLignesNomenclature().add(LigneNomenclature.builder().article(Article.builder().id(99L).build()).build());

        when(modeleRepository.existsByReference("ROBE-01")).thenReturn(false);
        when(modeleMapper.toEntity(request)).thenReturn(entity);
        when(articleRepository.findById(99L)).thenReturn(Optional.empty()); // L'article est introuvable

        assertThatThrownBy(() -> modeleService.createModele(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Création du modèle impossible : l'article avec l'ID 99 n'existe pas");
    }

    // ==========================================
    // 2. TESTS DE LECTURE (READ)
    // ==========================================

    @Test
    @DisplayName("Doit lister tous les modèles")
    void getAllModeles_shouldReturnList() {
        Modele m1 = Modele.builder().id(1L).build();
        ModeleResponse res1 = new ModeleResponse(1L, "R1", null, null, null, null, null);

        when(modeleRepository.findAll()).thenReturn(List.of(m1));
        when(modeleMapper.toResponse(m1)).thenReturn(res1);

        List<ModeleResponse> result = modeleService.getAllModeles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Doit retourner un modèle par son ID")
    void getModeleById_shouldReturnModele() {
        Modele m1 = Modele.builder().id(1L).build();
        ModeleResponse res1 = new ModeleResponse(1L, "R1", null, null, null, null, null);

        when(modeleRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(modeleMapper.toResponse(m1)).thenReturn(res1);

        ModeleResponse result = modeleService.getModeleById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Doit lever une exception si le modèle n'est pas trouvé (GET)")
    void getModeleById_shouldThrowException_whenNotFound() {
        when(modeleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> modeleService.getModeleById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le modèle avec l'ID 1 est introuvable.");
    }

    // ==========================================
    // 3. TESTS DE MODIFICATION (UPDATE)
    // ==========================================

    @Test
    @DisplayName("Doit mettre à jour le modèle et nettoyer la nomenclature")
    void updateModele_shouldSuccess() {
        // Arrange
        Long id = 1L;

        // L'ASTUCE ICI : On crée un vrai spy sur la liste !
        List<LigneNomenclature> lignesSpy = spy(new ArrayList<>());
        lignesSpy.add(new LigneNomenclature()); // On simule une ancienne ligne à supprimer

        Modele existingModele = Modele.builder()
                .id(id)
                .reference("ROBE-OLD")
                .lignesNomenclature(lignesSpy)
                .build();

        ModeleRequest request = new ModeleRequest("ROBE-NEW", "Nom", "Desc", BigDecimal.TEN, List.of());
        ModeleResponse expectedResponse = new ModeleResponse(id, "ROBE-NEW", "Nom", "Desc", BigDecimal.TEN, BigDecimal.TEN, List.of());

        when(modeleRepository.findById(id)).thenReturn(Optional.of(existingModele));
        when(modeleRepository.existsByReference("ROBE-NEW")).thenReturn(false); // La nouvelle référence est dispo

        // On simule MapStruct qui met à jour l'entité.
        // L'action clé qu'on teste c'est que le clear() a bien eu lieu juste avant.
        doAnswer(invocation -> {
            // Pour simuler la réalité, MapStruct ajouterait la nouvelle ligne de nomenclature ici
            Article dummyArticle = Article.builder().id(55L).build();
            LigneNomenclature newLigne = LigneNomenclature.builder().article(dummyArticle).build();
            existingModele.getLignesNomenclature().add(newLigne);
            return null;
        }).when(modeleMapper).updateEntityFromRequest(request, existingModele);

        Article vraiArticle = Article.builder().id(55L).build();
        when(articleRepository.findById(55L)).thenReturn(Optional.of(vraiArticle));
        when(modeleRepository.save(existingModele)).thenReturn(existingModele);
        when(modeleMapper.toResponse(existingModele)).thenReturn(expectedResponse);

        // Act
        ModeleResponse actualResponse = modeleService.updateModele(id, request);

        // Assert
        verify(lignesSpy).clear(); // LE TEST ULTIME : On vérifie que la liste a bien été vidée !
        verify(modeleMapper).updateEntityFromRequest(request, existingModele);
        verify(articleRepository).findById(55L); // Vérifie que l'article a été re-vérifié en BDD
        verify(modeleRepository).save(existingModele);
        assertThat(actualResponse.reference()).isEqualTo("ROBE-NEW");
    }

    @Test
    @DisplayName("Doit lever une exception lors de l'update si la nouvelle référence est déjà prise")
    void updateModele_shouldThrowException_whenNewRefExists() {
        Long id = 1L;
        Modele existingModele = Modele.builder().id(id).reference("ROBE-01").build();
        ModeleRequest request = new ModeleRequest("ROBE-02", "Nom", "Desc", BigDecimal.TEN, List.of());

        when(modeleRepository.findById(id)).thenReturn(Optional.of(existingModele));
        when(modeleRepository.existsByReference("ROBE-02")).thenReturn(true); // Conflit !

        assertThatThrownBy(() -> modeleService.updateModele(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà utilisée");

        verify(modeleRepository, never()).save(any());
    }
}
