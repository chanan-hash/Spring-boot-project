package com.capitolis.taskmanagementapi;

import com.capitolis.taskmanagementapi.model.Task;
import com.capitolis.taskmanagementapi.model.TaskStatus;
import com.capitolis.taskmanagementapi.service.TaskService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
@Order(2)  // Run after BrowserOpener (which is Order 1 by default)
public class TaskConsoleManager implements CommandLineRunner {

    @Value("${app.console.task-manager.enabled:false}")
    private boolean enabled;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BrowserOpener browserOpener;  // INJECT BrowserOpener

    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (!enabled) {
            return;
        }

        Thread.sleep(2000);  // Wait for browser opener to finish

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 Welcome to Task Manager Console!");
        System.out.println("=".repeat(60));

        while (true) {
            showMenu();

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1 -> viewAllTasks();
                    case 2 -> addTask();
                    case 3 -> updateTask();
                    case 4 -> deleteTask();
                    case 5 -> markTaskComplete();
                    case 6 -> searchTasks();
                    case 7 -> filterByStatus();
                    case 8 -> {
                        // NEW OPTION: Go back to browser menu
                        System.out.println("🌐 Opening Browser Menu...");
                        browserOpener.selectStrategyInteractively();
                        // When user returns from browser menu, show task menu again
                    }
                    case 0 -> {
                        System.out.println("👋 Exiting Task Manager. API still running!");
                        return;
                    }
                    case 9 -> {
                        System.out.println("👋 Shutting down application...");
                        System.exit(0);
                    }
                    default -> System.out.println("⚠️ Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Invalid input. Please enter a number.");
                scanner.nextLine();  // Clear buffer
            }
        }
    }

    private void showMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 TASK MANAGEMENT MENU");
        System.out.println("=".repeat(60));
        System.out.println("1. 📋 View All Tasks");
        System.out.println("2. ➕ Add New Task");
        System.out.println("3. ✏️ Update Task");
        System.out.println("4. 🗑️ Delete Task");
        System.out.println("5. ✅ Mark Task as Complete");
        System.out.println("6. 🔍 Search Tasks");
        System.out.println("7. 🎯 Filter by Status");
        System.out.println("8. 🌐 Browser Menu (open pages)");  // NEW OPTION
        System.out.println("0. ⬅️ Exit (API keeps running)");
        System.out.println("9. 🛑 Shutdown Application");
        System.out.println("=".repeat(60));
        System.out.print("Your choice: ");
    }

    private void viewAllTasks() {
        List<Task> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            System.out.println("\n📭 No tasks found. Add your first task!");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("📋 ALL TASKS");
        System.out.println("=".repeat(80));

        for (Task task : tasks) {
            printTask(task);
        }
    }

    private void addTask() {
        System.out.println("\n➕ ADD NEW TASK");
        System.out.println("-".repeat(60));

        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Description (optional, press Enter to skip): ");
        String description = scanner.nextLine();

        System.out.println("\nSelect Status:");
        System.out.println("1. TODO");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
        System.out.print("Choice (default 1): ");

        int statusChoice = 1;
        try {
            statusChoice = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
        }

        TaskStatus status = switch (statusChoice) {
            case 2 -> TaskStatus.IN_PROGRESS;
            case 3 -> TaskStatus.DONE;
            default -> TaskStatus.TODO;
        };

        System.out.print("Due Date (yyyy-MM-dd HH:mm, or press Enter to skip): ");
        String dueDateStr = scanner.nextLine();

        LocalDateTime dueDate = null;
        if (!dueDateStr.isBlank()) {
            try {
                dueDate = LocalDateTime.parse(dueDateStr, formatter);
            } catch (Exception e) {
                System.out.println("⚠️ Invalid date format. Skipping due date.");
            }
        }

        // Create task
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description.isBlank() ? null : description);
        task.setStatus(status);
        task.setDueDate(dueDate);

        Task created = taskService.createTask(task);

        System.out.println("\n✅ Task created successfully!");
        printTask(created);
    }

    private void updateTask() {
        viewAllTasks();

        System.out.print("\nEnter Task ID to update: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        var taskOpt = taskService.getTaskById(id);
        if (taskOpt.isEmpty()) {
            System.out.println("❌ Task not found!");
            return;
        }

        Task task = taskOpt.get();

        System.out.println("\n✏️ UPDATING TASK: " + task.getTitle());
        System.out.println("(Press Enter to keep current value)");
        System.out.println("-".repeat(60));

        System.out.print("New Title [" + task.getTitle() + "]: ");
        String title = scanner.nextLine();
        if (!title.isBlank()) task.setTitle(title);

        System.out.print("New Description [" + (task.getDescription() != null ? task.getDescription() : "none") + "]: ");
        String description = scanner.nextLine();
        if (!description.isBlank()) task.setDescription(description);

        System.out.println("\nCurrent Status: " + task.getStatus());
        System.out.println("1. TODO  2. IN_PROGRESS  3. DONE");
        System.out.print("New Status (or Enter to keep): ");
        String statusStr = scanner.nextLine();
        if (!statusStr.isBlank()) {
            int statusChoice = Integer.parseInt(statusStr);
            task.setStatus(switch (statusChoice) {
                case 2 -> TaskStatus.IN_PROGRESS;
                case 3 -> TaskStatus.DONE;
                default -> TaskStatus.TODO;
            });
        }

        Task updated = taskService.updateTask(id, task);
        System.out.println("\n✅ Task updated successfully!");
        printTask(updated);
    }

    private void deleteTask() {
        viewAllTasks();

        System.out.print("\nEnter Task ID to delete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        var taskOpt = taskService.getTaskById(id);
        if (taskOpt.isEmpty()) {
            System.out.println("❌ Task not found!");
            return;
        }

        System.out.print("⚠️ Are you sure you want to delete this task? (yes/no): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
            taskService.deleteTask(id);
            System.out.println("✅ Task deleted successfully!");
        } else {
            System.out.println("❌ Deletion cancelled.");
        }
    }

    private void markTaskComplete() {
        viewAllTasks();

        System.out.print("\nEnter Task ID to mark as complete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        try {
            Task completed = taskService.markTaskAsComplete(id);
            System.out.println("\n✅ Task marked as DONE!");
            printTask(completed);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void searchTasks() {
        System.out.print("\n🔍 Enter search keyword: ");
        String keyword = scanner.nextLine();

        List<Task> tasks = taskService.searchTasksByTitle(keyword);

        if (tasks.isEmpty()) {
            System.out.println("📭 No tasks found matching: " + keyword);
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔍 SEARCH RESULTS for: " + keyword);
        System.out.println("=".repeat(80));

        for (Task task : tasks) {
            printTask(task);
        }
    }

    private void filterByStatus() {
        System.out.println("\n🎯 SELECT STATUS:");
        System.out.println("1. TODO");
        System.out.println("2. IN_PROGRESS");
        System.out.println("3. DONE");
        System.out.print("Choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        TaskStatus status = switch (choice) {
            case 2 -> TaskStatus.IN_PROGRESS;
            case 3 -> TaskStatus.DONE;
            default -> TaskStatus.TODO;
        };

        List<Task> tasks = taskService.getTasksByStatus(status);

        if (tasks.isEmpty()) {
            System.out.println("📭 No " + status + " tasks found.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("🎯 TASKS WITH STATUS: " + status);
        System.out.println("=".repeat(80));

        for (Task task : tasks) {
            printTask(task);
        }
    }

    private void printTask(Task task) {
        String statusIcon = switch (task.getStatus()) {
            case TODO -> "⭕";
            case IN_PROGRESS -> "🔄";
            case DONE -> "✅";
        };

        System.out.println("\n" + statusIcon + " Task #" + task.getId() + " - " + task.getStatus());
        System.out.println("   Title: " + task.getTitle());
        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            System.out.println("   Description: " + task.getDescription());
        }
        if (task.getDueDate() != null) {
            System.out.println("   Due: " + task.getDueDate().format(formatter));
        }
        System.out.println("   Created: " + task.getCreatedAt().format(formatter));
        System.out.println("-".repeat(80));
    }
}