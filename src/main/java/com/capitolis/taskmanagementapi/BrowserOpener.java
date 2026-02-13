package com.capitolis.taskmanagementapi;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

/**
 * Auoto-opens the browser to http://localhost:8080/api/tasks when the application starts
 */

@Component
public class BrowserOpener implements CommandLineRunner{

    @Override
    public void run(String @NonNull ... args) {
        try {
            // Wait a moment for the server to fully start
            Thread.sleep(1000);

            String url = "http://localhost:8080/api/tasks";

            // Check if Desktop is supported
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(new URI(url));
                        System.out.println("Browser opened automatically: " + url);
                    } catch (Exception e) {
                        System.err.println("Failed to open browser: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Auto-open not supported. Please open manually: " + url);
            }
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
        }
    }

}
