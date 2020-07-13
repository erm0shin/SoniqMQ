package ru.innotechnum.testlistener.integration.sonic.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import progress.message.jclient.QueueConnectionFactory;
import ru.innotechnum.testlistener.integration.sonic.dto.XmlMessage;
import ru.innotechnum.testlistener.integration.sonic.listener.SonicListener;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.HashMap;

@EnableJms
@Configuration
@EnableConfigurationProperties(SonicIntegrationProperties.class)
public class SonicIntegrationConfiguration {

    public static final String SONIC_LOAN_RESPONSES_QUEUE = "sonic_loan_responses_queue";

    @Bean
    public ConnectionFactory sonicConnectionFactory(SonicIntegrationProperties sonicIntegrationProperties) throws JMSException {
        final QueueConnectionFactory factory = new QueueConnectionFactory();
        factory.setBrokerURL(sonicIntegrationProperties.getBrokerUrl());

        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(factory);
        cachingConnectionFactory.setSessionCacheSize(sonicIntegrationProperties.getCacheSize());
        return cachingConnectionFactory;
    }

    @Bean
    public DestinationResolver sonicDestinationResolver(SonicIntegrationProperties sonicIntegrationProperties) {
        return new DynamicDestinationResolver() {
            @Override
            protected Queue resolveQueue(Session session, String queueName) throws JMSException {
                if (SONIC_LOAN_RESPONSES_QUEUE.equals(queueName))
                    return super.resolveQueue(session, sonicIntegrationProperties.getResponseQueue());
                else
                    return super.resolveQueue(session, queueName);
            }
        };
    }

    @Bean
    public MessageConverter sonicMessageConverter() {
        final MarshallingMessageConverter converter = new MarshallingMessageConverter();
        converter.setTargetType(MessageType.TEXT);
        Jaxb2Marshaller marshaller = buildXMLMarshaller();
        converter.setMarshaller(marshaller);
        converter.setUnmarshaller(marshaller);
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

    @Bean
    public SonicListener sonicListener() {
        return new SonicListener();
    }

    private Jaxb2Marshaller buildXMLMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XmlMessage.class);
        marshaller.setMarshallerProperties(new HashMap<>() {{
            put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
        }});
        return marshaller;
    }

}
