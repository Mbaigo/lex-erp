package com.mbaigo.swingapp.service.customer.customer_service.exceptionshandler;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        // Optionnel : Pour renvoyer les détails des erreurs de validation (@NotBlank, etc.)
        Map<String, String> validationErrors
) {}
