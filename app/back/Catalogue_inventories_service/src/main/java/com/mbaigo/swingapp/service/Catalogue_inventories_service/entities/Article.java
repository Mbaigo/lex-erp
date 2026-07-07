package com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.UniteMesureEnum;
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
    private String reference;

    @Column(nullable = false, length = 150)
    private String designation;

    @Column(name = "stock_initial", nullable = false)// updatable = false protège cette valeur à vie
    private Integer stockInitial;

//    @Formula("""
//        (SELECT COALESCE(SUM(
//            CASE WHEN m.type = 'ENTREE' THEN m.quantite
//                 WHEN m.type = 'SORTIE' THEN -m.quantite
//                 ELSE 0 END
//        ), 0) + stock_initial
//        FROM stock_movement m
//        WHERE m.article_id = id)
//    """)

    // N'est plus une @Formula. Redevient une colonne classique.
    @Column(name = "stock_actuel", nullable = false)
    private Integer stockActuel;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(nullable = false)
    private Integer seuilAlerte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private UniteMesureEnum uniteMesure;

    // --- SÉCURITÉ JPA ---
    @PrePersist
    public void prePersist() {
        if (this.stockInitial == null && this.stockActuel != null) {
            this.stockInitial = this.stockActuel;
        }
    }

    // Getters et Setters...
}