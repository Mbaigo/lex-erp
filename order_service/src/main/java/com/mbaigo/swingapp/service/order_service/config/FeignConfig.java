package com.mbaigo.swingapp.service.order_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Si on a un jeton JWT en cours, on l'injecte dans la requête Feign
            if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
                String tokenValue = jwtAuthToken.getToken().getTokenValue();
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
            }
        };
    }
}
