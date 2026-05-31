package com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;

import com.mbaigo.swingapp.service.Catalogue_inventories_service.enums.TypeMovementEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class StockMovement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Article article;

    int quantite;

    @Enumerated(EnumType.STRING)
    private TypeMovementEnum type;

    BigDecimal prixUnitaire;

    String motif;

    LocalDate dateOperation;

    LocalDateTime createdAt;
    //Le cout total du mouvement (quantite * prixUnitaire) pour faciliter les rapports et analyses
    private BigDecimal priceTotal;
    private Integer stockAvantOperation;
    private Integer stockApresOperation;
}
