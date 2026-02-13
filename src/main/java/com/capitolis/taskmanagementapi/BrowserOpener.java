//package com.capitolis.taskmanagementapi;
//
//import org.jspecify.annotations.NonNull;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.awt.Desktop;
//import java.net.URI;
//
///**
// * Auoto-opens the browser to http://localhost:8080/api/tasks when the application starts
// */
//
//@Component
//public class BrowserOpener implements CommandLineRunner{
//
//    @Override
//    public void run(String @NonNull ... args) {
//        try {
//            // Wait a moment for the server to fully start
//            Thread.sleep(1000);
//
//            String url = "http://localhost:8080/api/tasks";
//
//            // Check if Desktop is supported
//            if (Desktop.isDesktopSupported()) {
//                Desktop desktop = Desktop.getDesktop();
//                if (desktop.isSupported(Desktop.Action.BROWSE)) {
//                    try {
//                        desktop.browse(new URI(url));
//                        System.out.println("Browser opened automatically: " + url);
//                    } catch (Exception e) {
//                        System.err.println("Failed to open browser: " + e.getMessage());
//                    }
//                }
//            } else {
//                System.out.println("Auto-open not supported. Please open manually: " + url);
//            }
//        } catch (InterruptedException e) {
//            System.err.println("Thread was interrupted: " + e.getMessage());
//            Thread.currentThread().interrupt();
//        } catch (Exception e) {
//            System.err.println("Unexpected error occurred: " + e.getMessage());
//        }
//    }
//
//}


package com.capitolis.taskmanagementapi;

import com.capitolis.taskmanagementapi.config.UrlStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;
import java.util.Map;
import java.util.Scanner;

@Component
public class BrowserOpener implements CommandLineRunner {

    // @Value allows us to inject configuration properties from application.properties or application.yml
    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${app.browser.auto-open:false}")
    private boolean autoOpen;

    @Value("${app.browser.strategy:apiEndpoint}")
    private String strategyName;

    @Value("${app.browser.interactive:false}")
    private boolean interactive;

    @Autowired // Injecting the Spring application context to access beans dynamically
    private ApplicationContext context; // This allows us to retrieve beans of type UrlStrategy at runtime, enabling dynamic selection of which URL to open based on configuration or user input.

    @Override
    public void run(String... args) throws Exception {
        if (!autoOpen) {
            System.out.println("Auto-open disabled. Enable in application.properties: app.browser.auto-open=true");
            return;
        }

        Thread.sleep(1500);  // Wait for server to fully start

        UrlStrategy strategy;

        if (interactive) {
            strategy = selectStrategyInteractively();
        } else {
            strategy = getStrategy(strategyName);
        }

        if (strategy != null) {
            openBrowser(strategy);
        }
    }

    private UrlStrategy selectStrategyInteractively() {
        Map<String, UrlStrategy> strategies = context.getBeansOfType(UrlStrategy.class);

        System.out.println("\n === Browser Auto-Open Options ===");
        System.out.println("Select which page to open:");


        // This loop iterates over the available UrlStrategy beans and prints them as options for the user to choose from. Each strategy is displayed with a number, its name, and the URL it will open when selected. The user can then input the corresponding number to select which page they want to open in their browser.
        int i = 1;
        for (Map.Entry<String, UrlStrategy> entry : strategies.entrySet()) {
            System.out.println(i + ". " + entry.getValue().getName() +
                    " - " + entry.getValue().getUrl(serverPort));
            i++;
        }
        System.out.println("0. Skip (don't open browser)");
        System.out.print("\nYour choice: ");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if (choice == 0) {
            System.out.println("Skipped browser auto-open");
            return null;
        }

        if (choice > 0 && choice <= strategies.size()) {
            return (UrlStrategy) strategies.values().toArray()[choice - 1];
        }

        System.out.println("Invalid choice. Skipping browser auto-open.");
        return null;
    }

    private UrlStrategy getStrategy(String name) {
        try {
            return context.getBean(name, UrlStrategy.class);
        } catch (Exception e) {
            System.out.println("Strategy '" + name + "' not found. Available: apiEndpoint, h2Console, homePage");
            return null;
        }
    }

    private void openBrowser(UrlStrategy strategy) {
        String url = strategy.getUrl(serverPort);

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                    System.out.println("Browser opened: " + strategy.getName() + " - " + url);
                } catch (Exception e) {
                    System.out.println("Failed to open browser: " + e.getMessage());
                }
            } else {
                System.out.println("BROWSE action not supported. Please open manually: " + url);
            }
        } else {
            System.out.println("Desktop not supported. Please open manually: " + url);
        }
    }
}