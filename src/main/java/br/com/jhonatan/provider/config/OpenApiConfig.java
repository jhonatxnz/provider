package br.com.jhonatan.provider.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Simple metadata only, no security scheme yet.
    // TODO: once authentication is implemented, add a SecurityScheme (e.g. bearerAuth)
    // here and reference it with @SecurityRequirement on the protected controllers.
    @Bean
    public OpenAPI providerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Provider API")
                        .description("API for customer registration and subscription management")
                        .version("v0.0.1")
                        .contact(new Contact().name("Jhonatan Willian dos Santos Silva").email("jw.jhonatan1705@gmail.com"))
                );
    }
}
