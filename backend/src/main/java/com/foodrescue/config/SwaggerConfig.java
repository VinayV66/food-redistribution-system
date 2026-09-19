package com.foodrescue.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI configuration.
 *
 * Access the API docs at:
 *   http://localhost:8080/swagger-ui.html
 *
 * The "bearerAuth" security scheme lets you paste your JWT token
 * into Swagger UI and test protected endpoints directly.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Food Rescue & Redistribution System API")
                        .description("REST API for connecting food donors with NGOs and volunteers " +
                                     "to reduce food waste.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Food Rescue Team")
                                .email("support@foodrescue.com")))
                // Add JWT bearer auth to all protected endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token (without 'Bearer ' prefix)")));
    }
}
