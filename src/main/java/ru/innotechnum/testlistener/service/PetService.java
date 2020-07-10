package ru.innotechnum.testlistener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.innotechnum.testlistener.gateway.petstore.api.PetGateway;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
public class PetService {

    private final static Logger LOG = LoggerFactory.getLogger(PetService.class);

    private final PetGateway petGateway;

    @Autowired
    public PetService(PetGateway petGateway) {
        this.petGateway = petGateway;
    }

    @PostConstruct
    public void test() {
        petGateway.findPetsByStatus(Pet.StatusEnum.AVAILABLE)
                .doOnNext(pets ->
                        LOG.info(
                                "Received pets, one of their is: {}",
                                pets.get(new Random().nextInt((pets.size()))))
                )
                .subscribe();
    }
}
