package ru.innotechnum.testlistener.gateway.petstore.api;

import reactor.core.publisher.Mono;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;

import java.util.List;

public interface PetGateway {

    Mono<List<Pet>> findPetsByStatus(Pet.StatusEnum status);

}
