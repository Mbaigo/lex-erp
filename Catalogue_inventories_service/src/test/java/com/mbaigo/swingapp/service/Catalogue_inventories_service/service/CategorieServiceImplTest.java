package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.CategorieResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.CategorieMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.CategorieRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl.CategorieServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorieServiceImplTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private CategorieMapper categorieMapper;

    @InjectMocks
    private CategorieServiceImpl categorieService;

    // ==========================================
    // TESTS POUR LA CRÉATION (CREATE)
    // ==========================================

    @Test
    @DisplayName("Doit créer une catégorie avec succès si le code est unique")
    void createCategorie_shouldSuccess_whenCodeIsUnique() {
        // Arrange
        CategorieRequest request = new CategorieRequest("TIS", "Tissus", "Desc");
        Categorie entity = Categorie.builder().code("TIS").nom("Tissus").build();
        CategorieResponse expectedResponse = new CategorieResponse(1L, "TIS", "Tissus", "Desc");

        when(categorieRepository.existsByCode("TIS")).thenReturn(false);
        when(categorieMapper.toEntity(request)).thenReturn(entity);
        when(categorieRepository.save(entity)).thenReturn(entity);
        when(categorieMapper.toResponse(entity)).thenReturn(expectedResponse);

        // Act
        CategorieResponse actualResponse = categorieService.createCategorie(request);

        // Assert
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.code()).isEqualTo("TIS");
        verify(categorieRepository, times(1)).save(entity); // Vérifie que l'enregistrement en BDD a eu lieu
    }

    @Test
    @DisplayName("Doit lever une exception si le code de la catégorie existe déjà lors de la création")
    void createCategorie_shouldThrowException_whenCodeAlreadyExists() {
        // Arrange
        CategorieRequest request = new CategorieRequest("TIS", "Tissus", "Desc");
        when(categorieRepository.existsByCode("TIS")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categorieService.createCategorie(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existe déjà");

        verify(categorieRepository, never()).save(any()); // Sécurité : on s'assure qu'aucune sauvegarde n'est tentée
    }

    // ==========================================
    // TESTS POUR LA LECTURE (READ)
    // ==========================================

    @Test
    @DisplayName("Doit retourner la liste complète des catégories")
    void getAllCategories_shouldReturnList() {
        // Arrange
        Categorie cat1 = Categorie.builder().id(1L).code("TIS").build();
        Categorie cat2 = Categorie.builder().id(2L).code("FIL").build();

        CategorieResponse res1 = new CategorieResponse(1L, "TIS", "Tissus", null);
        CategorieResponse res2 = new CategorieResponse(2L, "FIL", "Fils", null);

        when(categorieRepository.findAll()).thenReturn(List.of(cat1, cat2));
        when(categorieMapper.toResponse(cat1)).thenReturn(res1);
        when(categorieMapper.toResponse(cat2)).thenReturn(res2);

        // Act
        List<CategorieResponse> result = categorieService.getAllCategories();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CategorieResponse::code).containsExactly("TIS", "FIL");
    }

    @Test
    @DisplayName("Doit retourner une catégorie par son ID si elle existe")
    void getCategorieById_shouldReturnCategorie_whenIdExists() {
        // Arrange
        Long id = 1L;
        Categorie entity = Categorie.builder().id(id).code("TIS").build();
        CategorieResponse expectedResponse = new CategorieResponse(id, "TIS", "Tissus", null);

        when(categorieRepository.findById(id)).thenReturn(Optional.of(entity));
        when(categorieMapper.toResponse(entity)).thenReturn(expectedResponse);

        // Act
        CategorieResponse result = categorieService.getCategorieById(id);

        // Assert
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.code()).isEqualTo("TIS");
    }

    @Test
    @DisplayName("Doit lever une exception si la catégorie n'est pas trouvée par son ID")
    void getCategorieById_shouldThrowException_whenIdDoesNotExist() {
        // Arrange
        Long id = 99L;
        when(categorieRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categorieService.getCategorieById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("introuvable");
    }

    // ==========================================
    // TESTS POUR LA MODIFICATION (UPDATE)
    // ==========================================

    @Test
    @DisplayName("Doit mettre à jour la catégorie si le nouveau code est unique")
    void updateCategorie_shouldSuccess_whenNewCodeIsUnique() {
        // Arrange
        Long id = 1L;
        Categorie existingEntity = Categorie.builder().id(id).code("TIS").nom("Tissus").build();
        CategorieRequest request = new CategorieRequest("TIS-MOD", "Tissus Modifiés", "Desc");
        CategorieResponse expectedResponse = new CategorieResponse(id, "TIS-MOD", "Tissus Modifiés", "Desc");

        when(categorieRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        // On vérifie que le nouveau code n'est pas pris par une autre ligne en base
        when(categorieRepository.existsByCode("TIS-MOD")).thenReturn(false);
        when(categorieRepository.save(existingEntity)).thenReturn(existingEntity);
        when(categorieMapper.toResponse(existingEntity)).thenReturn(expectedResponse);

        // Act
        CategorieResponse actualResponse = categorieService.updateCategorie(id, request);

        // Assert
        verify(categorieMapper).updateEntityFromRequest(request, existingEntity); // Vérifie que MapStruct a été appelé pour faire le mapping
        verify(categorieRepository).save(existingEntity);
        assertThat(actualResponse.code()).isEqualTo("TIS-MOD");
    }

    @Test
    @DisplayName("Doit lever une exception lors de la modification si le nouveau code appartient déjà à une autre catégorie")
    void updateCategorie_shouldThrowException_whenNewCodeAlreadyExistsForAnotherCategorie() {
        // Arrange
        Long id = 1L;
        Categorie existingEntity = Categorie.builder().id(id).code("TIS").build();
        // L'utilisateur essaie de renommer en "FIL", mais "FIL" existe déjà pour l'ID 2
        CategorieRequest request = new CategorieRequest("FIL", "Fils", "");

        when(categorieRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(categorieRepository.existsByCode("FIL")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categorieService.updateCategorie(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà utilisé par une autre catégorie");

        verify(categorieRepository, never()).save(any());
    }
}
