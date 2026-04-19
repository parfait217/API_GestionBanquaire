package com.example.banque.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger/OpenAPI 3 pour la documentation automatique de l'API
 * 
 * Swagger UI accessible à : http://localhost:8080/swagger-ui.html
 * JSON OpenAPI accessible à : http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestion Bancaire")
                        .version("2.0")
                        .description("Documentation complète de l'API REST de gestion bancaire avec authentification JWT\n\n" +
                                "## 🔐 Authentification\n" +
                                "Cette API utilise **JWT (JSON Web Tokens)** pour l'authentification.\n\n" +
                                "### Flux d'authentification :\n" +
                                "1. S'inscrire sur `/api/auth/register`\n" +
                                "2. Se connecter sur `/api/auth/login` pour obtenir un token\n" +
                                "3. Utiliser le token dans le header `Authorization: Bearer <token>`\n\n" +
                                "### Token JWT\n" +
                                "- **Durée de vie** : 24 heures\n" +
                                "- **Algorithme** : HS512\n" +
                                "- **Format** : Bearer token dans Authorization header\n\n" +
                                "## 📡 Points d'accès\n" +
                                "- **Swagger UI** : http://localhost:8080/swagger-ui.html\n" +
                                "- **OpenAPI JSON** : http://localhost:8080/v3/api-docs\n" +
                                "- **Base URL** : http://localhost:8080")
                        .contact(new Contact()
                                .name("Équipe de Développement")
                                .email("dev@banque.local")
                                .url("https://github.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token obtenu via /api/auth/login\n\n" +
                                                "Insérer le token dans le format : Bearer <token>")));
    }
}
