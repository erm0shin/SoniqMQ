package ru.innotechnum.testlistener.integration;

import org.springframework.stereotype.Component;

@Component
public class SonicIntegrationProperties {
    private final String host = "";
    private final int port = 0;
    private final String queueManager = "";
    private final String channel = "";
    private final String requestQueue = "test";
    private final String responseQueue = "test";
    private final int cacheSize = 1;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public String getChannel() {
        return channel;
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
}
