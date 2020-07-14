package ru.innotechnum.testlistener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.innotechnum.testlistener.gateway.petstore.api.PetGateway;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;

import java.util.List;
import java.util.Random;

@Service
public class PetServiceImpl implements PetService {

    private final static Logger LOG = LoggerFactory.getLogger(PetServiceImpl.class);

    private final PetGateway petGateway;

    @Autowired
    public PetServiceImpl(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    @Override
    public Mono<List<Pet>> getPetInfo(Pet.StatusEnum status) {
        return petGateway.findPetsByStatus(status)
                .doOnNext(pets ->
                        LOG.info(
                                "Received pets, one of their is: {}",
                                pets.get(new Random().nextInt((pets.size()))))
                );
    }

}
