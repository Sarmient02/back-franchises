package co.com.bancolombia.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Franchises Backend API",
                version = "1.0.0",
                description = "API for Franchises Backend",
                contact = @Contact(
                        name = "Javier Sarmiento",
                        email = "javiersarmiento02@gmail.com",
                        url = "https://github.com/sarmient02"
                ),
                license = @License(
                        name = "No license"
                )
        )
)
public class OpenApiConfig {
    // La configuración se realiza mediante anotaciones
}