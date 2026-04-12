package com.mbaigo.swingapp.service.order_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_materiaux_commande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneMateriauCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    // Lien vers le catalog-inventory-service (L'article final choisi, US 5.2)
    @Column(nullable = false)
    private Long articleId;

    // La quantité nécessaire pour cette commande
    @Column(nullable = false)
    private Double quantite;

    // SNAPSHOT : On fige le prix du matériau au moment de l'achat
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaireSnapshot;

    // US 5.3 : Coût de cette ligne
    public BigDecimal getCoutLigne() {
        if (prixUnitaireSnapshot == null || quantite == null) {
            return BigDecimal.ZERO;
        }
        return prixUnitaireSnapshot.multiply(BigDecimal.valueOf(quantite));
    }
}
