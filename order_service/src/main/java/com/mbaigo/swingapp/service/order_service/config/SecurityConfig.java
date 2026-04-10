package com.mbaigo.swingapp.service.order_service.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // On désactive la protection CSRF car nous faisons une API REST stateless
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 1. On laisse la porte grande ouverte pour Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // 2. (Optionnel pour le dev) Si tu veux tester tes API sans t'embêter avec Keycloak tout de suite,
                        // tu peux décommenter la ligne suivante et commenter la 3ème :
                        // .requestMatchers("/api/**").permitAll()

                        // 3. Tout le reste (tes API) nécessite d'être connecté via Keycloak
                        .anyRequest().authenticated()
                )
                // On dit à Spring qu'il doit lire les tokens JWT envoyés par Keycloak
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
    }
}