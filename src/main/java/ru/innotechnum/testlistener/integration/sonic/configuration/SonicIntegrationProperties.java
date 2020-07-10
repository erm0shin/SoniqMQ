package ru.innotechnum.testlistener.integration.sonic.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "sonic")
@ConstructorBinding
public class SonicIntegrationProperties {
    private final String brokerUrl;
    private final String requestQueue;
    private final String responseQueue;
    private final int cacheSize;
    private final String correlationId;

    public SonicIntegrationProperties(String brokerUrl, String requestQueue, String responseQueue, int cacheSize, String correlationId) {
        this.brokerUrl = brokerUrl;
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
        this.cacheSize = cacheSize;
        this.correlationId = correlationId;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public String getRequestQueue() {
        return requestQueue;
    }

    public String getResponseQueue() {
        return responseQueue;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
