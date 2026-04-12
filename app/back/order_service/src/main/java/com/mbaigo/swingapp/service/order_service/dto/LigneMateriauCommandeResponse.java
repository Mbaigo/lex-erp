package com.mbaigo.swingapp.service.order_service.dto;

import java.math.BigDecimal;

public record LigneMateriauCommandeResponse(
        Long id,
        Long articleId,
        Double quantite,
        BigDecimal prixUnitaireSnapshot,
        BigDecimal coutLigne // Sera calculé dynamiquement par l'entité
) {}
