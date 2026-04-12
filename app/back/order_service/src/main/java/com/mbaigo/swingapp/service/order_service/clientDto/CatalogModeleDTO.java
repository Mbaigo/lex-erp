package com.mbaigo.swingapp.service.order_service.clientDto;

import java.math.BigDecimal;
import java.util.List;

public record CatalogModeleDTO(
        Long id,
        String reference,
        String nom,
        BigDecimal coutMainOeuvre, // C'est ce champ qu'on va "figer" (snapshot)
        List<CatalogLigneNomenclatureDTO> lignesNomenclature
) {}
