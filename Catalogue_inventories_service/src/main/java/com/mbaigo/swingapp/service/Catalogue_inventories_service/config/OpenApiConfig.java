package com.mbaigo.swingapp.service.Catalogue_inventories_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Catalogue & Stock - Atelier Couture ERP")
                        .description("Documentation des API REST pour la gestion des catégories, des articles et des mouvements d'inventaire.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Atelier Couture")
                                .email("admin@atelier-couture.com")));
    }
}
