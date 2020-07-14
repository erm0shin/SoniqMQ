package ru.innotechnum.testlistener.service;

import reactor.core.publisher.Mono;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;

import java.util.List;

public interface PetService {

    Mono<List<Pet>> getPetInfo(Pet.StatusEnum status);

}
