package com.capitolis.taskmanagementapi.service;

import com.capitolis.taskmanagementapi.model.Task;
import com.capitolis.taskmanagementapi.model.TaskStatus;
import com.capitolis.taskmanagementapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The service layer is responsible for containing business logic and interacting with the repository layer to perform operations on Task
 */


// Tells Spring: "This is a service component"
@Service // @Service annotation indicates that this class is a service component in the Spring framework, which is responsible for containing business logic and interacting with the repository layer to perform operations on Task entities. It allows us to define methods for creating, retrieving, updating, and deleting tasks, as well as any additional business logic related to task management.
public class TaskService {

    private final TaskRepository taskRepository; // TaskRepository is injected into the TaskService to allow it to perform database operations on Task entities. By using @Autowired, Spring will automatically inject an instance of TaskRepository when creating an instance of TaskService, enabling us to use the repository's methods for CRUD operations and custom queries on tasks.

    // Dependency Injection (Spring Core Magic!)

    /**
     * Spring automatically finds your TaskRepository and "injects" it into the constructor
     * You never write new TaskRepository() - Spring handles it
     * This is one of the core concepts your team lead mentioned!
     */
    @Autowired // @Autowired annotation is used to automatically inject the TaskRepository dependency into the TaskService class. This allows us to use the repository's methods for performing database operations on Task entities without needing to manually instantiate the repository.
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    // Create a new task and save it to the database
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Get all tasks from the database
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Get a task by ID -- Optional is a container object which may or may not contain a non-null value. If a value is present, isPresent() will return true and get() will return the value.
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Update a task
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id) // Finding the task according to ID
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // Actual update
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());

        // Saving the updated task back to the database
        return taskRepository.save(task);
    }

    // Delete a task from the database
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) { // checking if the task exists before trying to delete it.
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    // Finding method coming from the TaskRepository interface.
    // Get tasks by status
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status); // This method calls the findByStatus method defined in the TaskRepository interface, which is automatically implemented by Spring Data JPA based on the method name. It retrieves a list of tasks that match the specified status from the database.
    }

    // Search tasks by title
    public List<Task> searchTasksByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword); // This method calls the findBy title
    }

    // Mark task as complete
    public Task markTaskAsComplete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setStatus(TaskStatus.DONE);
        return taskRepository.save(task);
    }
}
