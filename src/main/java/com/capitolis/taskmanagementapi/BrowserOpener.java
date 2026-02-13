package com.capitolis.taskmanagementapi;

import com.capitolis.taskmanagementapi.config.UrlStrategy;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
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

    @Autowired
    private ApplicationContext context;

    @Autowired // Injecting the Spring application context to access beans dynamically
    private ConfigurableApplicationContext applicationContext; // This allows us to retrieve beans of type UrlStrategy at runtime, enabling dynamic selection of which URL to open based on configuration or user input.

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (!autoOpen) {
            System.out.println("Auto-open disabled. Enable in application.properties: app.browser.auto-open=true");
            return;
        }

        Thread.sleep(1500);  // Wait for server to fully start

        if (interactive) {
            selectStrategyInteractively();
        } else {
            UrlStrategy strategy = getStrategy(strategyName);
            if (strategy != null) {
                openBrowser(strategy);
            }
        }
    }

//    private void selectStrategyInteractively() {
//        Map<String, UrlStrategy> strategies = context.getBeansOfType(UrlStrategy.class);
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//            System.out.println("\n === Browser Auto-Open Options ===");
//            System.out.println("Select which page to open:");
//
//            // This loop iterates over the available UrlStrategy beans and prints them as options for the user to choose from. Each strategy is displayed with a number, its name, and the URL it will open when selected. The user can then input the corresponding number to select which page they want to open in their browser.
//            int i = 1;
//            for (Map.Entry<String, UrlStrategy> entry : strategies.entrySet()) {
//                System.out.println(i + ". " + entry.getValue().getName() +
//                        " - " + entry.getValue().getUrl(serverPort));
//                i++;
//            }
//            System.out.println("0. Continue without opening browser");
//            System.out.println("9. Exit application");
//            System.out.print("\nYour choice: ");
//
//            try {
//                int choice = scanner.nextInt();
//
//                if (choice == 0) {
//                    System.out.println("Continuing without opening browser...");
//                    break;
//                }
//
//                if (choice == 9) {
//                    System.out.println("Shutting down application gracefully...");
//                    scanner.close();
//                    // Graceful shutdown in separate thread
//                    new Thread(() -> {
//                        try {
//                            Thread.sleep(500);
//                            applicationContext.close();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }).start();
//                    break;
//                }
//
//                if (choice > 0 && choice <= strategies.size()) {
//                    UrlStrategy selected = (UrlStrategy) strategies.values().toArray()[choice - 1];
//                    openBrowser(selected);
//                    Thread.sleep(500);
//                } else {
//                    System.out.println("Invalid choice. Please try again.");
//                }
//            } catch (Exception e) {
//                System.out.println("Invalid input. Please enter a number.");
//                scanner.nextLine();
//            }
//        }
//    }

    private void selectStrategyInteractively() {
        Map<String, UrlStrategy> strategies = context.getBeansOfType(UrlStrategy.class);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n" + "=".repeat(60));  // Visual separator
            System.out.println(" === Browser Auto-Open Options ===");
            System.out.println("=".repeat(60));
            System.out.println("Select which page to open:");

            // This loop iterates over the available UrlStrategy beans and prints them as options for the user to choose from. Each strategy is displayed with a number, its name, and the URL it will open when selected. The user can then input the corresponding number to select which page they want to open in their browser.
            int i = 1;
            for (Map.Entry<String, UrlStrategy> entry : strategies.entrySet()) {
                System.out.println(i + ". " + entry.getValue().getName() +
                        " - " + entry.getValue().getUrl(serverPort));
                i++;
            }
            System.out.println("0. Continue without opening browser");
            System.out.println("9. Exit application");
            System.out.println("=".repeat(60));
            System.out.print("\nYour choice: ");

            try {
                int choice = scanner.nextInt();

                if (choice == 0) {
                    System.out.println("Continuing without opening browser...");
                    break;
                }

                if (choice == 9) {
                    System.out.println("Shutting down application gracefully...");
                    scanner.close();
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                            applicationContext.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                    break;
                }

                if (choice > 0 && choice <= strategies.size()) {
                    UrlStrategy selected = (UrlStrategy) strategies.values().toArray()[choice - 1];
                    openBrowser(selected);
                    System.out.println("\nSQL queries may appear below (this is normal - your API is working!)");
                    Thread.sleep(1000);  // Longer delay to let SQL finish
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private UrlStrategy getStrategy(String name) {
        try {
            return context.getBean(name, UrlStrategy.class);
        } catch (Exception e) {
            System.out.println("Strategy '" + name + "' not found. Available: apiEndpoint, h2DatabaseConsole, homePage");
            return null;
        }
    }

    private void openBrowser(UrlStrategy strategy) {
        String url = strategy.getUrl(serverPort);

        // Try Java Desktop API first
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                    System.out.println("Browser opened: " + strategy.getName() + " - " + url);
                    return;
                } catch (Exception e) {
                    System.out.println(" Desktop API failed, trying platform-specific method...");
                }
            }
        }

        // Fallback: Use platform-specific commands
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", "start", url);
            } else if (os.contains("mac")) {
                processBuilder = new ProcessBuilder("open", url);
            } else if (os.contains("nix") || os.contains("nux")) {
                processBuilder = new ProcessBuilder("xdg-open", url);
            } else {
                System.out.println("Unsupported OS. Please open manually: " + url);
                return;
            }

            processBuilder.start();
            System.out.println("Browser opened: " + strategy.getName() + " - " + url);

        } catch (Exception e) {
            System.out.println("Failed to open browser: " + e.getMessage());
            System.out.println("Please open manually: " + url);
        }
    }
}