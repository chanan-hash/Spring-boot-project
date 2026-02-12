package com.capitolis.taskmanagementapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// Task entity representing a task in the task management system

@Entity // @Entity annotation indicates that this class is a JPA entity
@Table(name = "tasks") // @Table annotation specifies the name of the database table to be used for mapping
@Data // @Data annotation from Lombok generates getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor // @NoArgsConstructor annotation from Lombok generates a no-argument constructor
@AllArgsConstructor // @AllArgsConstructor annotation from Lombok generates a constructor with arguments for all fields

public class Task {

    @Id // @Id annotation specifies the primary key of the entity helping for database operations
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue annotation specifies the primary key generation strategy
    private Long id; // Unique identifier for the task

    // @Column means the column that will be created in the database for this field.
    @Column(nullable = false) // @Column annotation specifies the column details in the database, nullable = false means this field cannot be null
    private String title; // Title of the task

    @Column(length = 1000) // @Column annotation with length attribute specifies the maximum length of the column in the database
    private String description; // Description of the task

    @Enumerated(EnumType.STRING) // @Enumerated annotation specifies that the enum should be stored as a string in the database
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO; // Status of the task, represented as an enum

    @Column(name = "due_date") // @Column annotation with name attribute specifies the name of the column in the database
    private LocalDateTime dueDate; // Due date of the task

    @Column(name = "created_at", nullable = false, updatable = false) // @Column annotation with name, nullable, and updatable attributes specifies the column details in the database, updatable = false means this field cannot be updated after creation
    private LocalDateTime createdAt; // Java DataTime API class representing the date and time when the task was created

    @Column(name = "updated_at") // @Column annotation with name attribute specifies the name of the column in the database
    private LocalDateTime updatedAt; // For being able to track when the task was last updated and not on the creation time


    @PrePersist // @PrePersist annotation indicates that the annotated method should be called before the entity is persisted (saved) to the database. This is typically used to set default values or perform any necessary actions before the entity is stored in the database.
    // For inheritance purposes, the method is protected, meaning it can be accessed by subclasses but not from outside the class hierarchy. This allows subclasses to override this method if needed while still ensuring that it is called before the entity is persisted to the database.
    protected void onCreate() { //
        this.createdAt = LocalDateTime.now(); // Method annotated with @PrePersist is called before the entity is persisted (saved) to the database, setting the createdAt field to the current date and time
        this.updatedAt = LocalDateTime.now(); // Setting the updatedAt field to the current date and time when the task is created
    }

    @PreUpdate // for update before the entity is updated in the database, this method will be called to set the updatedAt field to the current date and time. This allows us to keep track of when the task was last modified.
    protected void onUpdate() { // Method annotated with @PreUpdate is called before the entity is updated in the database, setting the updatedAt field to the current date and time
        this.updatedAt = LocalDateTime.now(); // Setting the updatedAt field to the current date and time when the task is updated
    }

}
