package org.farhan.runners.surefire.webmvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;

@Configuration
@ComponentScan(basePackages = { "org.farhan.mbt.controller", "org.farhan.mbt.exception", "org.farhan.mbt.service" })
@TestPropertySource("classpath:application-surefire.properties")
public class MockitoTestConfig {
    @Bean
    public JmsTemplate jmsTemplate() {
        return org.mockito.Mockito.mock(JmsTemplate.class);
    }
}
