package com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ModeleTest {

    @Test
    @DisplayName("Doit calculer correctement le coût de base (Main d'œuvre + Matériaux)")
    void getCoutDeBase_shouldCalculateTotalCorrectly() {
        // 1. Arrange (Préparation des données)
        Article soie = Article.builder().prixAchat(new BigDecimal("10.00")).build(); // 10€ / mètre
        Article bouton = Article.builder().prixAchat(new BigDecimal("2.50")).build(); // 2.50€ / pièce

        LigneNomenclature ligneSoie = LigneNomenclature.builder().article(soie).quantiteNecessaire(3.0).build(); // 3m = 30€
        LigneNomenclature ligneBouton = LigneNomenclature.builder().article(bouton).quantiteNecessaire(4.0).build(); // 4 pièces = 10€

        Modele robe = Modele.builder()
                .coutMainOeuvre(new BigDecimal("50.00")) // 50€ de main d'œuvre
                .lignesNomenclature(List.of(ligneSoie, ligneBouton))
                .build();

        // 2. Act (Exécution de la méthode)
        BigDecimal coutTotal = robe.getCoutDeBase();

        // 3. Assert (Vérification) : 50 + 30 + 10 = 90.00
        assertThat(coutTotal).isEqualByComparingTo(new BigDecimal("90.00"));
    }
}
