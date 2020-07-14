package ru.innotechnum.testlistener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationProperties;
import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
public class SonicServiceImpl implements SonicService {

    private final JmsTemplate sonicJmsTemplate;
    private final SonicIntegrationProperties sonicIntegrationProperties;

    @Autowired
    public SonicServiceImpl(JmsTemplate sonicJmsTemplate, SonicIntegrationProperties sonicIntegrationProperties) {
        this.sonicJmsTemplate = sonicJmsTemplate;
        this.sonicIntegrationProperties = sonicIntegrationProperties;
    }

    @Override
    public void sendResponse(XmlMessage xmlMessage) {
        sonicJmsTemplate.convertAndSend(
                sonicIntegrationProperties.getResponseQueue(),
                xmlMessage,
                m -> {
                    m.setJMSCorrelationID(sonicIntegrationProperties.getCorrelationId());
                    return m;
                }
        );
    }

//    @PostConstruct
//    public void test() {
//        final XmlMessage xmlMessage = new XmlMessage();
//        xmlMessage.setName("xmlName");
//        xmlMessage.setBalance(new BigDecimal(10));
//        xmlMessage.setDescription("xmlDescription");
//
//        sonicJmsTemplate.convertAndSend(
//                sonicIntegrationProperties.getRequestQueue(),
//                xmlMessage,
//                m -> {
//                    m.setJMSCorrelationID(sonicIntegrationProperties.getCorrelationId());
//                    return m;
//                }
//        );
//    }

}
