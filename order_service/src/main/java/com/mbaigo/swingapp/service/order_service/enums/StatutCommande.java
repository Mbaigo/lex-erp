package com.mbaigo.swingapp.service.order_service.enums;

public enum StatutCommande {
    CREEE,          // 0. Dès la création
    EN_CONFECTION,  // 1. L'artisan coupe et coud
    ESSAYAGE,       // 2. Le client vient tester
    TERMINEE,       // 3. Facturée et livrée
    ANNULEE         // 4. (US 6.2) Annulée, matériaux retournés au stock
}
