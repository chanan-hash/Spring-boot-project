package com.capitolis.taskmanagementapi.repository;

// The Repository handles all database operations (save, find, update, delete)
// TaskRepository is an interface that extends JpaRepository, providing CRUD operations for the Task entity. It allows us to perform database operations on Task entities without needing to write boilerplate code for common operations like saving, finding, updating, and deleting tasks.

import com.capitolis.taskmanagementapi.model.Task;
import com.capitolis.taskmanagementapi.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// CRUD means Create, Read, Update, Delete - these are the basic operations that can be performed on data in a database. By extending JpaRepository, TaskRepository inherits methods for performing these operations on Task entities, such as save(), findById(), findAll(), deleteById(), etc.

/**
 * By extending JpaRepository<Task, Long>, we automatically get these methods
 * taskRepository.save(task);           // Insert or update
 * taskRepository.findById(1L);         // Find by ID
 * taskRepository.findAll();            // Get all tasks
 * taskRepository.deleteById(1L);       // Delete by ID
 * taskRepository.count();              // Count all tasks
 * taskRepository.existsById(1L);       // Check if exists
 *
 * Spring generates SQL queries automatically based on method names!
 *
 * findByStatus(TaskStatus.TODO)
 * // Spring generates: SELECT * FROM tasks WHERE status = 'TODO'
 *
 * findByTitleContainingIgnoreCase("meeting")
 * // Spring generates: SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%meeting%')
 *
 * findAllByOrderByDueDateAsc()
 * // Spring generates: SELECT * FROM tasks ORDER BY due_date ASC
 */

public interface TaskRepository extends JpaRepository<Task, Long> { // inheriting from JpaRepository, which provides basic CRUD operations for Task entities with Long as the type of the primary key (id)

    // Spring automatically implements these methods based on method names!

    // Find all tasks by status
    List<Task> findByStatus(TaskStatus status);

    // Find tasks by title containing a keyword (case-insensitive)
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // Find all tasks ordered by due date
    List<Task> findAllByOrderByDueDateAsc();

    // Check if a task with a specific title exists
    boolean existsByTitle(String title);
}
