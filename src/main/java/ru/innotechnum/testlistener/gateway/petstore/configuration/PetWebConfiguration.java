package ru.innotechnum.testlistener.gateway.petstore.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PetWebConfiguration {

    private final String petUrl;

    @Autowired
    public PetWebConfiguration(@Value("${pet_url}") String petUrl) {
        this.petUrl = petUrl;
    }

    @Bean
    public WebClient petWebClient() {
        return WebClient.builder()
                .baseUrl(petUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
