package ru.innotechnum.testlistener.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PetServiceTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private PetService petService;

    @BeforeAll
    public static void setUp() throws IOException {
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
        WireMock.configureFor("localhost", 9999);
        String petResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/pets/responses/available_pets.json")));

        stubFor(WireMock.get(urlPathEqualTo("/findByStatus"))
                .withQueryParam("status", equalTo("available"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(petResponse)));

        stubFor(WireMock.get(urlPathEqualTo("/findByStatus"))
                .withQueryParam("status", equalTo("pending"))
                .willReturn(aResponse()
                        .withStatus(500)));

    }

    @AfterAll
    public static void stopServer() {
        wireMockServer.stop();
    }

    @Test
    public void successfullyGetPets() {
        final List<Pet> pets = petService.getPetInfo(Pet.StatusEnum.AVAILABLE).block();
        assertNotNull(pets);
        assertEquals(3, pets.size());
        assertNotNull(pets.stream().filter(pet -> pet.getId().equals(15435006003037L)).findFirst());
    }

    @Test
    public void unsuccessfullyGetPets() {
        RuntimeException exception = assertThrows(
                WebClientResponseException.InternalServerError.class,
                () -> petService.getPetInfo(Pet.StatusEnum.PENDING).block()
        );
        assertTrue(exception.getMessage().contains("500 Internal Server Error"));
    }

}
