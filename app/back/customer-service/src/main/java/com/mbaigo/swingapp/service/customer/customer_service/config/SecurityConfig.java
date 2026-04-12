package com.mbaigo.swingapp.service.customer.customer_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permet d'utiliser @PreAuthorize("hasRole('GERANT')") sur tes controllers
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF car on est en architecture stateless (API REST)
                .csrf(AbstractHttpConfigurer::disable)
                // Pas de session serveur (tout est dans le token JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Règles des routes
                .authorizeHttpRequests(auth -> auth
                        // Autoriser l'accès public à la documentation Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Toutes les autres requêtes nécessitent d'être authentifié
                        .anyRequest().authenticated()
                )
                // Configuration en tant que Resource Server recevant des JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    /**
     * Le petit "Hack" Senior :
     * Par défaut, Spring cherche les rôles dans un champ 'scope' ou 'scp' du JWT.
     * Mais Keycloak range les rôles dans 'realm_access.roles'.
     * Ce convertisseur fait le pont entre les deux.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

            converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                // 1. On va chercher le bloc "realm_access" dans le token Keycloak
                Map<String, Object> realmAccess = jwt.getClaim("realm_access");

                if (realmAccess == null || !realmAccess.containsKey("roles")) {
                    return Collections.emptyList();
                }

                // 2. On extrait la liste des rôles
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");

                // 3. On ajoute le préfixe "ROLE_" exigé par Spring Security
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toList());
            });

            return converter;
        }
}
