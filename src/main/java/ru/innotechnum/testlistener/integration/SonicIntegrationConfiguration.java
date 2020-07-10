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
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import progress.message.jclient.QueueConnectionFactory;
import progress.message.jclient.TopicConnectionFactory;
import ru.innotechnum.testlistener.dto.Message;
import ru.innotechnum.testlistener.dto.XmlMessage;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;

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

//    @Bean
//    public MessageConverter sonicMessageConverter() {
//        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setTargetType(MessageType.TEXT);
//        converter.setTypeIdPropertyName("_type");
//        converter.setObjectMapper(objectMapper);
//        return converter;
//    }

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

    private static Jaxb2Marshaller buildXMLMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XmlMessage.class);
        marshaller.setMarshallerProperties(new HashMap<>() {{
            put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // set more properties here...
        }});
        return marshaller;
    }

    public static void main(String[] args) throws JAXBException, IOException {
        XmlMessage message = new XmlMessage();
        message.setName("test");
        message.setBalance(new BigDecimal(10));
        message.setDescription("test");

        Jaxb2Marshaller marshaller = buildXMLMarshaller();

        String xml = marshallXml(marshaller, message);
        System.out.println(xml);
        XmlMessage object = unmarshallXml(marshaller, new ByteArrayInputStream(xml.getBytes()));
        System.out.println(object);
    }

    // marshalls one object (of your bound classes) into a String.
    public static <T> String marshallXml(Marshaller marshaller, final T obj) throws JAXBException, IOException {
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);
        marshaller.marshal(obj, result);
        return sw.toString();
    }

    // (tries to) unmarshall(s) an InputStream to the desired object.
    public static <T> T unmarshallXml(Unmarshaller unmarshaller, final InputStream xml) throws JAXBException, IOException {
        return (T) unmarshaller.unmarshal(new StreamSource(xml));
    }

}
