package com.mbaigo.swingapp.service.order_service.dto;

import com.mbaigo.swingapp.service.order_service.enums.StatutCommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeResponse(
        Long id,
        String reference,
        LocalDateTime dateCreation,
        Long clientId,
        Long modeleId,
        BigDecimal coutMainOeuvreSnapshot,
        BigDecimal prixTotal,
        StatutCommande statut,
        List<LigneMateriauCommandeResponse> materiaux // Ajout de la liste !
) {}
