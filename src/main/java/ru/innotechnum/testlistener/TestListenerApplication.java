package ru.innotechnum.testlistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.innotechnum.testlistener.integration.sonic.configuration.SonicIntegrationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SonicIntegrationProperties.class)
public class TestListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestListenerApplication.class, args);
    }

}
