package com.capitolis.taskmanagementapi.controller;

import com.capitolis.taskmanagementapi.model.Task;
import com.capitolis.taskmanagementapi.model.TaskStatus;
import com.capitolis.taskmanagementapi.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * The TaskController is very similar to Flask's routes.py
 * Handling Restful API endpoints for Task management
 */

@RestController // @RestController annotation indicates that this class is a RESTful web controller in the Spring framework. It combines @Controller and @ResponseBody, allowing us to handle HTTP requests and return JSON responses directly from the methods in this class. This is where we will define our API endpoints for managing tasks.
@RequestMapping("/api/tasks") // @RequestMapping annotation at the class level specifies that all endpoints in this controller will be prefixed with "/api/tasks". For example, if we define a method with @GetMapping("/"), it will be accessible at "/api/tasks/".
@CrossOrigin(origins = "*") // @CrossOrigin annotation allows cross-origin requests to this controller. By setting origins = "*", we are allowing requests from any origin, which is useful during development when the frontend and backend may be running on different domains or ports. In a production environment, you would typically restrict this to specific origins for security reasons.

public class TaskController {

    private final TaskService taskService; // TaskService is injected into the TaskController to allow it to perform business logic and interact with the repository layer for managing tasks. By using @Autowired, Spring will automatically inject an instance of TaskService when creating an instance of TaskController, enabling us to use the service's methods for creating, retrieving, updating, and deleting tasks.


    @Autowired // @Autowired annotation is used to automatically inject the TaskService dependency into the TaskController class. This allows us to use the service's methods for performing business logic and interacting with the repository layer for managing tasks without needing to manually instantiate the service.
    // Simple constructor-based dependency injection.
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET /api/tasks - Get all tasks
    @GetMapping // @GetMapping annotation indicates that this method will handle HTTP GET requests to
    public ResponseEntity<List<Task>> getAllTasks() { // We don't want only tp return List<Task> because we also want to include HTTP status codes (or more HTTP features) in our response. By using ResponseEntity<List<Task>>, we can return both the list of tasks and the appropriate HTTP status code (e.g., 200 OK) in a single response object.
        List<Task> tasks = taskService.getAllTasks(); // Calling the service method to get all tasks from the database
        return ResponseEntity.ok(tasks); // Returning the list of tasks wrapped in a ResponseEntity with HTTP status 200 OK
//        return new ResponseEntity<>(tasks, HttpStatus.OK); // Returning the list of tasks wrapped in a ResponseEntity with HTTP status 200 OK
    }

    // GET /api/tasks/{id} - Get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // POST /api/tasks - Create new task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) { // @RequestBody annotation indicates that the task object will be populated with the data from the request body when a POST request is made to this endpoint. This allows us to receive a JSON representation of a Task in the request body and automatically convert it from json into a Task object that we can use in our service layer to create a new task in the database.
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }


    // PUT /api/tasks/{id} - Update existing task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) { // PathVariable annotation indicates that the id parameter will be extracted from the URL path when a PUT request is made to this endpoint. This allows us to specify which task we want to update by including its ID in the URL.
        try {
            Task updatedTask = taskService.updateTask(id, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/tasks/{id} - Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/tasks/status/{status} - Get tasks by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    // GET /api/tasks/search?keyword=xyz - Search tasks by title
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String keyword) { // @RequestParam annotation indicates that the keyword parameter will be extracted from the query string of the URL when a GET request is made to this endpoint. This allows us to search for tasks by including a keyword in the query string, such as /api/tasks/search?keyword=meeting, which will trigger this method and pass "meeting" as the value of the keyword parameter.
        List<Task> tasks = taskService.searchTasksByTitle(keyword);
        return ResponseEntity.ok(tasks);
    }


    // PATCH - Partial update
    // PATCH /api/tasks/{id}/complete - Mark task as complete
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskAsComplete(@PathVariable Long id) {
        try {
            Task completedTask = taskService.markTaskAsComplete(id);
            return ResponseEntity.ok(completedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
