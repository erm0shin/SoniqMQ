package ru.innotechnum.testlistener.integration.sonic;

import org.awaitility.core.ConditionTimeoutException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;
import ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationProperties;
import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;
import ru.innotechnum.testlistener.service.PetService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = SonicTestConfiguration.class)
public class SonicIntegrationTest {

    @Autowired
    private JmsTemplate testJmsTemplate;
    @Autowired
    private SonicIntegrationProperties sonicIntegrationProperties;
    @MockBean
    private PetService petService;

    @Test
    public void successfullyReceiveAndSendMessage() {
        Mockito.when(petService.getPetInfo(Mockito.any()))
                .thenReturn(Mono.just(Collections.singletonList(createPet())));

        final XmlMessage xmlMessage = buildTestMessage();
        sendTestMessage(xmlMessage, sonicIntegrationProperties.getCorrelationId());

        await()
                .atLeast(Duration.ofMillis(100))
                .atMost(Duration.ofSeconds(5))
                .with()
                .pollInterval(Duration.ofMillis(100))
                .until(
                        () -> testJmsTemplate.receiveAndConvert(sonicIntegrationProperties.getResponseQueue()),
                        Matchers.equalTo(xmlMessage)
                );
    }

    @Test
    public void unsuccessfullySendMessage() {
        Mockito.doThrow(new RuntimeException("testException"))
                .when(petService).getPetInfo(Mockito.any());

        final XmlMessage xmlMessage = buildTestMessage();
        sendTestMessage(xmlMessage, sonicIntegrationProperties.getCorrelationId());

        assertThrows(
                ConditionTimeoutException.class,
                () -> await()
                        .atLeast(Duration.ofMillis(100))
                        .atMost(Duration.ofSeconds(5))
                        .with()
                        .pollInterval(Duration.ofMillis(100))
                        .until(
                                () -> testJmsTemplate.receiveAndConvert(sonicIntegrationProperties.getResponseQueue()),
                                Matchers.notNullValue()
                        )
        );
    }

    @Test
    public void unsuccessfullyGetMessage() {
        Mockito.when(petService.getPetInfo(Mockito.any()))
                .thenReturn(Mono.just(Collections.singletonList(createPet())));

        final XmlMessage xmlMessage = buildTestMessage();

        sendTestMessage(xmlMessage, "invalidCorrelationId");

        assertThrows(
                ConditionTimeoutException.class,
                () -> await()
                        .atLeast(Duration.ofMillis(100))
                        .atMost(Duration.ofSeconds(5))
                        .with()
                        .pollInterval(Duration.ofMillis(100))
                        .until(
                                () -> testJmsTemplate.receiveAndConvert(sonicIntegrationProperties.getResponseQueue()),
                                Matchers.notNullValue()
                        )
        );
    }

    private Pet createPet() {
        final Pet testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("testPet");
        return testPet;
    }

    private XmlMessage buildTestMessage() {
        final XmlMessage xmlMessage = new XmlMessage();
        xmlMessage.setName("testXmlName");
        xmlMessage.setBalance(new BigDecimal(1));
        xmlMessage.setDescription("testXmlDescription");
        return xmlMessage;
    }

    private void sendTestMessage(XmlMessage message, String correlationId) {
        testJmsTemplate.convertAndSend(
                sonicIntegrationProperties.getRequestQueue(),
                message,
                m -> {
                    m.setJMSCorrelationID(correlationId);
                    return m;
                }
        );
    }
}
