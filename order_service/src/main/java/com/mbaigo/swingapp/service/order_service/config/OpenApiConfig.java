package com.mbaigo.swingapp.service.order_service.config;

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
                        .title("API Order service- Atelier Couture ERP")
                        .description("Documentation des API REST pour Gérer le cycle de vie de la commande, orchestrer le débit du stock (via événements) et gérer la facturation.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Atelier Couture")
                                .email("admin@atelier-couture.com")));
    }
}
