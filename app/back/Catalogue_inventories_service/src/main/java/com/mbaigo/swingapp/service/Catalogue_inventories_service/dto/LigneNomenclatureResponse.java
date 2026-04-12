package com.mbaigo.swingapp.service.Catalogue_inventories_service.dto;

import java.math.BigDecimal;

public record LigneNomenclatureResponse(
        Long id,
        ArticleResponse article, // On renvoie tout le détail de l'article
        Double quantiteNecessaire,
        BigDecimal coutLigne // Calculé dynamiquement (Quantité * Prix unitaire)
) {}
