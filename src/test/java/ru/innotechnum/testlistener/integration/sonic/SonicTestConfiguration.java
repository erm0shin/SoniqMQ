package ru.innotechnum.testlistener.integration.sonic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationConfiguration;
import ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationProperties;

import javax.jms.ConnectionFactory;

//@EnableJms
@TestConfiguration
//@EnableConfigurationProperties(SonicIntegrationProperties.class)
@Import(SonicIntegrationConfiguration.class)
public class SonicTestConfiguration {

    private static final String testBrokerUrl = "vm://localhost?broker.persistent=false";

//    @Bean
//    @Primary
//    public SonicIntegrationProperties siteIntegrationProperties() {
//        return new SonicIntegrationProperties(
//                testBrokerUrl,
//                "testRequestQueue",
//                "testResponseQueue",
//                1,
//                "testCorId"
//        );
//    }

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
