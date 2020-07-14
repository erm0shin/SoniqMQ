package ru.innotechnum.testlistener.integration.sonic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;

@TestConfiguration
public class SonicTestConfiguration {

    private static final String testBrokerUrl = "vm://localhost?broker.persistent=false";

    @Bean
    @Primary
    public ConnectionFactory sonicConnectionFactory() {
        return new ActiveMQConnectionFactory(testBrokerUrl);
    }

    @Bean
    public JmsTemplate testJmsTemplate(
            ConnectionFactory sonicConnectionFactory,
            MessageConverter sonicMessageConverter
    ) {
        final JmsTemplate jmsTemplate = new JmsTemplate(sonicConnectionFactory);
        jmsTemplate.setMessageConverter(sonicMessageConverter);
        return jmsTemplate;
    }

}
