package com.devdivision.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition; // Now we will use this!
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "RentMate API REST", 
        version = "1.0", 
        description = "API REST for managing rentals and related activities.",
        contact = @Contact(name = "Loc Dzai", email = ""), 
        license = @License(name = "Apache License Version 2.0", url = "https://www.apache.org/licesen.html")
    ),
    security = @SecurityRequirement(name = "BearerAuth") // Apply globally here
)
@SecurityScheme(
    name = "BearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Enter your JWT token from the login endpoint. Format: just paste the token (no 'Bearer ' prefix needed)"
)
public class OpenApiConfig {

}