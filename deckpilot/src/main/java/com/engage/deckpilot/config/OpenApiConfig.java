package com.engage.deckpilot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deckPilotOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DeckPilot API")
                        .version("1.0.0")
                        .description("API para geração, gerenciamento, validação e avaliação de decks de Yu-Gi-Oh!.")
                        .contact(new Contact()
                                .name("DeckPilot")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Ambiente local")
                ));
    }
}
