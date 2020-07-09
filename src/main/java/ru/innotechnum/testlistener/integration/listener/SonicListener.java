package ru.innotechnum.testlistener.integration.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.innotechnum.testlistener.dto.Message;

import static ru.innotechnum.testlistener.integration.SonicIntegrationConfiguration.SONIC_LOAN_REQUESTS_QUEUE;

@Component
public class SonicListener {

    private static final Logger LOG = LoggerFactory.getLogger(SonicListener.class);

    @JmsListener(containerFactory = "sonicListenerContainerFactory", destination = SONIC_LOAN_REQUESTS_QUEUE)
    public void onMessage(Message message) {
        LOG.info("Received message: {}", message);
    }

}
