package ru.innotechnum.testlistener.integration.sonic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;

import static ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationConfiguration.SONIC_LOAN_RESPONSES_QUEUE;

public class SonicListener {

    private static final Logger LOG = LoggerFactory.getLogger(SonicListener.class);

    @JmsListener(
            containerFactory = "sonicListenerContainerFactory",
            destination = SONIC_LOAN_RESPONSES_QUEUE,
            selector = "JMSCorrelationID='${sonic.correlation_id}'"
    )
    public void onMessage(XmlMessage xmlMessage) {
        LOG.info("Received xml message: {}", xmlMessage);
    }

}
