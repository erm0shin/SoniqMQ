package ru.innotechnum.testlistener.integration.sonic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.innotechnum.testlistener.gateway.petstore.dto.Pet;
import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;
import ru.innotechnum.testlistener.service.PetService;
import ru.innotechnum.testlistener.service.SonicService;

import static ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationConfiguration.SONIC_LOAN_REQUESTS_QUEUE;

@Component
public class SonicListenerImpl implements SonicListener {

    private static final Logger LOG = LoggerFactory.getLogger(SonicListenerImpl.class);

    private final PetService petService;
    private final SonicService sonicService;

    public SonicListenerImpl(PetService petService, SonicService sonicService) {
        this.petService = petService;
        this.sonicService = sonicService;
    }

    @Override
    @JmsListener(
            containerFactory = "sonicListenerContainerFactory",
            destination = SONIC_LOAN_REQUESTS_QUEUE,
            selector = "JMSCorrelationID='${sonic.correlation_id}'"
    )
    public void onMessage(XmlMessage xmlMessage) {
        LOG.info("Received xml message: {}", xmlMessage);

        petService.getPetInfo(Pet.StatusEnum.AVAILABLE)
                .flatMap(response -> {
                    Mono<XmlMessage> blockingWrapper = Mono.fromRunnable(() -> sonicService.sendResponse(xmlMessage));
                    return blockingWrapper.subscribeOn(Schedulers.boundedElastic());
                })
                .subscribe();
    }

}
