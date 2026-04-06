package com.mbaigo.swingapp.service.order_service.entities;

import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference; // Ex: CMD-2026-001

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Lien vers le customer-service
    @Column(nullable = false)
    private Long clientId;

    // Lien vers le catalog-inventory-service
    @Column(nullable = false)
    private Long modeleId;

    // SNAPSHOT : On fige le coût de la main d'œuvre au moment de la commande
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coutMainOeuvreSnapshot;

    // US 5.3 : Le prix total figé
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCommande statut;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneMateriauCommande> materiaux = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = StatutCommande.CREEE;
        }
    }

    // Méthode métier pour l'US 5.3
    public void calculerPrixTotal() {
        BigDecimal totalMateriaux = materiaux.stream()
                .map(LigneMateriauCommande::getCoutLigne)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.prixTotal = this.coutMainOeuvreSnapshot.add(totalMateriaux);
    }
}
