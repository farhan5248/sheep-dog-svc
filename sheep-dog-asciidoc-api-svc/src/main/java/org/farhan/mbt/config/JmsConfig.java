package org.farhan.mbt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;

@Configuration
@EnableJms
public class JmsConfig {

    public static final String SOURCE_FILES_QUEUE = "source-files-queue";
    
    @Value("${spring.artemis.host}")
    private String artemisHost;
    
    @Value("${spring.artemis.port}")
    private int artemisPort;
    
    @Value("${spring.artemis.user}")
    private String artemisUser;
    
    @Value("${spring.artemis.password}")
    private String artemisPassword;
    
    @Bean
    @Profile("default") // Only create this bean when not in the test profile
    public ConnectionFactory jmsConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://" + artemisHost + ":" + artemisPort);
        factory.setUser(artemisUser);
        factory.setPassword(artemisPassword);
        
        // Wrap in a caching connection factory
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
        cachingFactory.setTargetConnectionFactory(factory);
        cachingFactory.setSessionCacheSize(10);
        cachingFactory.setCacheConsumers(false);
        cachingFactory.setCacheProducers(true);
        
        return cachingFactory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        
        // Configure ObjectMapper with JavaTimeModule for handling Java 8 date/time types
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(objectMapper);
        
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory jmsConnectionFactory, MessageConverter messageConverter) {
        JmsTemplate template = new JmsTemplate(jmsConnectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory jmsConnectionFactory,
            MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsConnectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrency("1"); // Set min-max concurrency
        return factory;
    }
}
