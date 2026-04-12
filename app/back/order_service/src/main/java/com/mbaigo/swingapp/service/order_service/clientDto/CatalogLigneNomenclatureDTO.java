package com.mbaigo.swingapp.service.order_service.clientDto;

import java.math.BigDecimal;

public record CatalogLigneNomenclatureDTO(
        Long id,
        CatalogArticleDTO article,
        Double quantiteNecessaire,
        BigDecimal coutLigne
) {}
