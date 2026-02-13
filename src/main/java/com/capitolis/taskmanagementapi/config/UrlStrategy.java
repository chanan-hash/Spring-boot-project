package com.capitolis.taskmanagementapi.config;

/**
 * This interface is a marker for URL strategies.
 * We are aiming to have few implementations of this interface, each representing a different strategy for handling URLs.
 *
 * // Option 1: API endpoint
 * String url = "http://localhost:8080/api/tasks";
 *
 * // Option 2: H2 Database Console
 * String url = "http://localhost:8080/h2-console";
 *
 * // Option 3: Future frontend page
 * String url = "http://localhost:8080/index.html";
 */

public interface UrlStrategy {

    String getUrl(int port);
    String getName();

}
