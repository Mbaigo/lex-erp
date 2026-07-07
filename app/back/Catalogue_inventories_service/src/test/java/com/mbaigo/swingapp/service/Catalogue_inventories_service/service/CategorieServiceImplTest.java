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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    @DisplayName("Devrait retourner une page de catégories triées par nom")
    void shouldReturnPaginatedCategories() {
        // 1. GIVEN (Données simulées)
        int page = 0;
        int size = 5;
        Categorie categorie = new Categorie();
        categorie.setNom("Mercerie");

        CategorieResponse response = new CategorieResponse(1L, "MERC", "Mercerie", "Accessoires");

        Page<Categorie> categoriePage = new PageImpl<>(List.of(categorie));

        // Mocking des appels
        when(categorieRepository.findAll(any(Pageable.class))).thenReturn(categoriePage);
        when(categorieMapper.toResponse(any(Categorie.class))).thenReturn(response);

        // 2. WHEN (Appel de la méthode)
        Page<CategorieResponse> result = categorieService.getAllCategories(page, size);

        // 3. THEN (Vérifications)
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).nom()).isEqualTo("Mercerie");

        // Vérification du PageRequest (Trier par nom ascendant)
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(categorieRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(page);
        assertThat(capturedPageable.getPageSize()).isEqualTo(size);
        assertThat(capturedPageable.getSort().getOrderFor("nom").getDirection()).isEqualTo(Sort.Direction.ASC);
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
