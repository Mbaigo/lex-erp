package com.mbaigo.swingapp.service.Catalogue_inventories_service.service;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.ArticleResponse;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.dto.reStock.StockMovementRequest;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Article;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.entities.Categorie;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.mappers.ArticleMapper;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.ArticleRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.repositories.CategorieRepository;
import com.mbaigo.swingapp.service.Catalogue_inventories_service.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Test
    @DisplayName("Doit décrémenter le stock si la quantité est suffisante (US 3.3)")
    void updateStock_shouldDecreaseStock_whenValidDebit() {
        // Arrange
        Article articleEnBase = Article.builder().reference("TIS-01").quantiteEnStock(10.0).build();
        when(articleRepository.findByReference("TIS-01")).thenReturn(Optional.of(articleEnBase));
        when(articleRepository.save(any(Article.class))).thenReturn(articleEnBase);

        // Act
        articleService.updateStock("TIS-01", new StockMovementRequest(3.0, true,""));

        // Assert
        assertThat(articleEnBase.getQuantiteEnStock()).isEqualTo(7.0);
        verify(articleRepository, times(1)).save(articleEnBase); // Vérifie que le save a bien été appelé
    }

    @Test
    @DisplayName("Doit bloquer le débit et lever une exception si le stock est insuffisant (US 3.3)")
    void updateStock_shouldThrowException_whenInsufficientStock() {
        // Arrange
        Article articleEnBase = Article.builder().reference("TIS-01").quantiteEnStock(2.0).build();
        when(articleRepository.findByReference("TIS-01")).thenReturn(Optional.of(articleEnBase));

        // Act & Assert
        assertThatThrownBy(() -> articleService.updateStock("TIS-01", new StockMovementRequest(5.0, true, "")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuffisant");

        verify(articleRepository, never()).save(any()); // Sécurité : on vérifie que la BDD n'a pas été touchée
    }

    @Test
    @DisplayName("Doit créer un article avec une catégorie valide")
    void createArticle_shouldSuccess() {
        // Arrange
        ArticleRequest request = new ArticleRequest("REF1", "Art1", 10.0, BigDecimal.TEN, 2.0, 1L);
        Categorie cat = Categorie.builder().id(1L).build();
        Article article = new Article();

        when(articleRepository.existsByReference("REF1")).thenReturn(false);
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(articleMapper.toEntity(request)).thenReturn(article);
        when(articleRepository.save(article)).thenReturn(article);

        // Act
        articleService.createArticle(request);

        // Assert
        verify(articleRepository).save(article);
        assertThat(article.getCategorie()).isEqualTo(cat);
    }

    @Test
    @DisplayName("Doit retourner uniquement les articles sous le seuil d'alerte")
    void getArticlesEnAlerte_shouldReturnFilteredList() {
        // Arrange
        Article a1 = Article.builder().reference("A1").build();
        when(articleRepository.findArticlesEnAlerte()).thenReturn(List.of(a1));
        when(articleMapper.toResponse(a1)).thenReturn(new ArticleResponse(1L, "A1", null, null, null, null, null));

        // Act
        List<ArticleResponse> results = articleService.getArticlesEnAlerte();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).reference()).isEqualTo("A1");
    }
}
