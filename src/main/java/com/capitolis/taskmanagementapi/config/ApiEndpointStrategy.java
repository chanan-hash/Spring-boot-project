package com.capitolis.taskmanagementapi.config;

import org.springframework.stereotype.Component;

@Component("apiEndpoint") // This annotation allows us to inject this specific implementation by name when needed
public class ApiEndpointStrategy implements UrlStrategy {

    @Override
    public String getUrl(int port) {
        return "http://localhost:" + port + "/api/tasks";
    }

    @Override
    public String getName() {
        return "API Endpoint";
    }
}