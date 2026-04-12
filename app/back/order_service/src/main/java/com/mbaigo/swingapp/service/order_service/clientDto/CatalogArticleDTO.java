package com.mbaigo.swingapp.service.order_service.clientDto;

import java.math.BigDecimal;

public record CatalogArticleDTO(
        Long id,
        String reference,
        String designation,
        BigDecimal prixAchat // C'est ce champ qu'on va "figer" (snapshot) dans la commande
) {}
