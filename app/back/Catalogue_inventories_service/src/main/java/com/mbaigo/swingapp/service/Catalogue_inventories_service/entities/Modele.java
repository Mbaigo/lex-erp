package com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modeles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modele {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference; // Ex: MOD-ROBE-SOIR-01

    @Column(nullable = false, length = 150)
    private String nom; // Ex: Robe de Soirée "Éclat"

    @Column(length = 500)
    private String description;

    // US 4.1 : Coût de main-d'œuvre fixe pour ce modèle
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coutMainOeuvre;

    // US 4.2 : La liste des matériaux nécessaires (La Nomenclature / BOM)
    // CascadeType.ALL permet de sauvegarder les lignes en même temps que le modèle
    // orphanRemoval = true supprime une ligne de la BDD si on l'enlève de la liste
    @OneToMany(mappedBy = "modele", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneNomenclature> lignesNomenclature = new ArrayList<>();

    // Méthode utilitaire pour calculer le coût total (Main d'œuvre + Matériaux)
    public BigDecimal getCoutDeBase() {
        BigDecimal coutMateriaux = lignesNomenclature.stream()
                .map(LigneNomenclature::getCoutLigne)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return coutMainOeuvre.add(coutMateriaux);
    }
}
