package com.mbaigo.swingapp.service.Catalogue_inventories_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class StockMovement {

    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Article article;

    int quantite;

    boolean isDebit; // sortie ou entrée

    BigDecimal prixUnitaire;

    String motif;

    LocalDate dateOperation;

    LocalDateTime createdAt;
}
