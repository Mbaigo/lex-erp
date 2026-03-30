package com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_nomenclature")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneNomenclature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers le modèle (La Robe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modele_id", nullable = false)
    private Modele modele;

    // Lien vers la matière première (Le Tissu, Le Bouton)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // La quantité de cet article nécessaire pour fabriquer le modèle (ex: 3.5 mètres)
    @Column(nullable = false)
    private Double quantiteNecessaire;

    // Calcule le coût de cette ligne (Quantité * Prix d'achat unitaire de l'article)
    public BigDecimal getCoutLigne() {
        if (article == null || article.getPrixAchat() == null) {
            return BigDecimal.ZERO;
        }
        return article.getPrixAchat().multiply(BigDecimal.valueOf(quantiteNecessaire));
    }
}
