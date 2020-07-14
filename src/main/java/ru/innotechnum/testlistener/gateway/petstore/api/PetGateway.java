package ru.innotechnum.testlistener.gateway.petstore.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetGateway {

    private final WebClient petWebClient;

    @Autowired
    public PetGateway(WebClient petWebClient) {
        this.petWebClient = petWebClient;
    }

    public Mono<List<Pet>> findPetsByStatus(final Pet.StatusEnum status) {
        return petWebClient.get().uri(uriBuilder -> uriBuilder
                .path("/findByStatus")
                .queryParam("status", status)
                .build())
                .retrieve()
                .bodyToFlux(Pet.class)
                .collect(Collectors.toList());
    }
}
