package com.capitolis.taskmanagementapi.config;

import org.springframework.stereotype.Component;

@Component("h2DatabaseConsole")  // Unique name
public class H2ConsoleStrategy implements UrlStrategy {

    @Override
    public String getUrl(int port) {
        return "http://localhost:" + port + "/h2-console";
    }

    @Override
    public String getName() {
        return "H2 Database Console";
    }
}