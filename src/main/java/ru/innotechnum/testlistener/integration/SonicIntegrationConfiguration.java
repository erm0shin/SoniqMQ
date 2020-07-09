package ru.innotechnum.testlistener.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import progress.message.jclient.QueueConnectionFactory;
import progress.message.jclient.TopicConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

@Configuration
@EnableJms
public class SonicIntegrationConfiguration {

    public static final String SONIC_LOAN_REQUESTS_QUEUE = "sonic_loan_requests_queue";

    private final ObjectMapper objectMapper;

    @Autowired
    public SonicIntegrationConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Bean
    public ConnectionFactory sonicConnectionFactory(SonicIntegrationProperties sonicIntegrationProperties) throws JMSException {
        final QueueConnectionFactory factory = new QueueConnectionFactory();
//        JmsListenerContainerFactory f = new TopicConnectionFactory();
//        final TopicConnectionFactory factory = new TopicConnectionFactory();
        factory.setBrokerURL("tcp://localhost:2506");
        factory.setClientID("DErmoshin");
        factory.setDefaultPassword("super_man7");
        return factory;

//        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(factory);
//        cachingConnectionFactory.setSessionCacheSize(sonicIntegrationProperties.getCacheSize());
//        return cachingConnectionFactory;
    }

    @Bean
    public DestinationResolver sonicDestinationResolver(SonicIntegrationProperties sonicIntegrationProperties) {
        return new DynamicDestinationResolver() {
            @Override
            protected Queue resolveQueue(Session session, String queueName) throws JMSException {
                if (SONIC_LOAN_REQUESTS_QUEUE.equals(queueName))
                    return super.resolveQueue(session, sonicIntegrationProperties.getRequestQueue());
                else
                    return super.resolveQueue(session, queueName);
            }
        };
    }

    @Bean
    public MessageConverter sonicMessageConverter() {
        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory sonicListenerContainerFactory(
            ConnectionFactory sonicConnectionFactory,
            DestinationResolver sonicDestinationResolver,
            MessageConverter sonicMessageConverter
    ) {
        final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sonicConnectionFactory);
        factory.setDestinationResolver(sonicDestinationResolver);
        factory.setConcurrency("3-10");
        factory.setMessageConverter(sonicMessageConverter);
        return factory;
    }

    @Bean
    public JmsTemplate sonicJmsTemplate(
            ConnectionFactory sonicConnectionFactory,
            MessageConverter sonicMessageConverter
    ) {
        final JmsTemplate jmsTemplate = new JmsTemplate(sonicConnectionFactory);
        jmsTemplate.setMessageConverter(sonicMessageConverter);
        return jmsTemplate;
    }

}
