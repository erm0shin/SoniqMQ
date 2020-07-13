package ru.innotechnum.testlistener.integration.sonic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationProperties;
import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;

import java.math.BigDecimal;

@SpringBootTest
@ContextConfiguration(classes = SonicTestConfiguration.class)
public class SonicIntegrationTest {

    private final JmsTemplate testJmsTemplate;
    private final SonicIntegrationProperties sonicIntegrationProperties;

    @Autowired
    public SonicIntegrationTest(JmsTemplate testJmsTemplate, SonicIntegrationProperties sonicIntegrationProperties) {
        this.testJmsTemplate = testJmsTemplate;
        this.sonicIntegrationProperties = sonicIntegrationProperties;
    }

    @Test
    public void test() {
        final XmlMessage xmlMessage = buildTestMessage();

        testJmsTemplate.convertAndSend(
                sonicIntegrationProperties.getRequestQueue(),
                xmlMessage,
                m -> {
                    m.setJMSCorrelationID(sonicIntegrationProperties.getCorrelationId());
                    return m;
                }
        );
    }

    private XmlMessage buildTestMessage() {
        final XmlMessage xmlMessage = new XmlMessage();
        xmlMessage.setName("testXmlName");
        xmlMessage.setBalance(new BigDecimal(1));
        xmlMessage.setDescription("testXmlDescription");
        return xmlMessage;
    }
}
