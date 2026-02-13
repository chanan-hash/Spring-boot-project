package com.capitolis.taskmanagementapi.config;

import org.springframework.stereotype.Component;

@Component("homePage")
public class HomePageStrategy implements UrlStrategy {

    @Override
    public String getUrl(int port) {
        return "http://localhost:" + port;
    }

    @Override
    public String getName() {
        return "Home Page";
    }
}