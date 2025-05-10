package org.example.oopproject1.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        // Server information
        Server devServer = new Server();
        devServer.setUrl("https://jobportaldb-api.onrender.com");
        devServer.setDescription("Server URL in Development environment");

        // Contact information
        Contact contact = new Contact();
        contact.setName("Job Portal API");
        contact.setEmail("info@example.com");
        contact.setUrl("https://www.example.com");

        // License information
        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        // API information
        Info info = new Info()
                .title("Job Portal API Documentation")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for the Job Portal application.")
                .license(mitLicense);

        // Add JWT security scheme
        Components components = new Components()
                .addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}