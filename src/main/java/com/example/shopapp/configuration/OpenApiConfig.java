package com.example.shopapp.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Tran Hoang Duy",
                        email = "duytrieudong@gmail.com",
                        url = "https://facebook.com/thduy2003"
                ),
                description = "OpenApi documentation for shop app Project",
                title = "OpenApi specification - Tran Hoang Duy",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://facebook.com"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8088"
                ),
                @Server(
                        description = "Build production",
                        url = "https://springboot-shopapp.onrender.com"
                )

        },
        security = {
                @SecurityRequirement(
                        name = "Security"
                )
        }
)
@SecurityScheme(
        name = "Security",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

