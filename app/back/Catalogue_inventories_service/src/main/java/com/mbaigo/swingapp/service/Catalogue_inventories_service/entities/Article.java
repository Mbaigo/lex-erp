package com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference; // Ex: TIS-SOIE-001

    @Column(nullable = false, length = 150)
    private String designation; // Ex: Rouleau de Soie rouge

    @Column(nullable = false)
    private Double quantiteEnStock; // Pour l'US 3.1

    @Column(precision = 10, scale = 2)
    private BigDecimal prixAchat; // Pour l'US 3.1

    @Column(nullable = false)
    private Double seuilAlerte; // Pour l'US 3.2

    // Relation : Un article appartient forcément à une catégorie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    // MAGIE POUR L'US 3.3 : Verrouillage optimiste
    // Si 2 requêtes essaient de modifier le stock en même temps, la 2ème échouera proprement.
    @Version
    private Long version;
}
