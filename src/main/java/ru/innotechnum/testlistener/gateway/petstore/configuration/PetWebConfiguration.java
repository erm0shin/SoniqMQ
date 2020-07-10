package ru.innotechnum.testlistener.gateway.petstore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PetWebConfiguration {
    @Bean
    public WebClient petWebClient() {
        return WebClient.builder()
                .baseUrl("https://petstore.swagger.io/v2/pet")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
