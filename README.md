# Task Management API

A RESTful API for managing tasks built with Spring Boot, demonstrating modern Java enterprise development practices.

## 📋 Table of Contents
- [Technologies Used](#technologies-used)
- [Project Architecture](#project-architecture)
- [Setup Instructions](#setup-instructions)
- [Project Structure](#project-structure)
- [Layer Explanations](#layer-explanations)
- [Key Concepts](#key-concepts)
- [Configuration](#configuration)
- [Next Steps](#next-steps)

---

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming language |
| **Maven** | - | Dependency management & build tool |
| **Spring Boot** | Latest | Framework that simplifies Spring development |
| **Spring Data JPA** | - | Database operations without writing SQL |
| **Spring Core** | - | Dependency Injection & application context |
| **H2 Database** | - | In-memory database for development |
| **Lombok** | - | Reduces boilerplate code |

---

## 🏗️ Project Architecture

We follow a **3-Layer Architecture** pattern:

```
┌─────────────────────────────────────────┐
│         MODEL LAYER                     │
│  (Database structure & entities)        │
│  - Defines data structure               │
│  - Maps Java objects to DB tables       │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│      REPOSITORY LAYER                   │
│  (Database operations - CRUD)           │
│  - Handles all DB operations            │
│  - Auto-generates SQL queries           │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│       SERVICE LAYER                     │
│  (Business logic)                       │
│  - Processes & validates data           │
│  - Contains business rules              │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│       CONTROLLER LAYER                  │
│  (REST API endpoints)                   │
│  - Handles HTTP requests                │
│  - Routes requests to services          │
└─────────────────────────────────────────┘
```

---

## 🚀 Setup Instructions

### Prerequisites
- Java 21 (Amazon Corretto or any OpenJDK distribution)
- IntelliJ IDEA Community Edition (or any Java IDE)
- Maven (bundled with IntelliJ)
- Git

### Installation Steps

1. **Install Java 21**
   - Download Amazon Corretto 21 from: https://aws.amazon.com/corretto/
   - Set `JAVA_HOME` environment variable
   - Verify: `java -version`

2. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd task-management-api
   ```

3. **Open in IntelliJ**
   - File → Open → Select project folder
   - IntelliJ will automatically import Maven dependencies

4. **Run the application**
   - Open `TaskManagementApiApplication.java`
   - Click the green play button
   - Or run: `mvn spring-boot:run`

5. **Access H2 Console** (to view database)
   - URL: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:taskdb`
   - Username: `sa`
   - Password: (leave empty)

---

## 📁 Project Structure

```
task-management-api/
├── src/
│   ├── main/
│   │   ├── java/com/capitolis/taskmanagementapi/
│   │   │   ├── model/                    # Entity classes
│   │   │   │   ├── Task.java             # Task entity
│   │   │   │   └── TaskStatus.java       # Enum for task statuses
│   │   │   ├── repository/               # Data access layer
│   │   │   │   └── TaskRepository.java   # Database operations interface
│   │   │   ├── service/                  # Business logic layer
│   │   │   │   └── TaskService.java      # Task business logic
│   │   │   ├── controller/               # REST API layer (to be added)
│   │   │   │   └── TaskController.java   # API endpoints
│   │   │   └── TaskManagementApiApplication.java  # Main application
│   │   └── resources/
│   │       └── application.properties    # Configuration file
│   └── test/                             # Test classes
├── pom.xml                               # Maven dependencies
└── README.md                             # This file
```

---

## 🔍 Layer Explanations

### 1️⃣ Model Layer (Entity)

**Location:** `src/main/java/.../model/`

**Purpose:** Defines the structure of data and maps Java objects to database tables.

#### Task.java
```java
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Key Features:**
- `@Entity` - Marks class as a database table
- `@Id` + `@GeneratedValue` - Auto-increment primary key
- `@Data` (Lombok) - Auto-generates getters, setters, toString, equals, hashCode
- `@PrePersist` / `@PreUpdate` - Automatically set timestamps

**Database Table Generated:**
```sql
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(50) NOT NULL,
    due_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

#### TaskStatus.java
```java
public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}
```

---

### 2️⃣ Repository Layer (Data Access)

**Location:** `src/main/java/.../repository/`

**Purpose:** Handles all database operations. Spring Data JPA automatically implements this interface!

#### TaskRepository.java
```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByTitleContainingIgnoreCase(String keyword);
    List<Task> findAllByOrderByDueDateAsc();
    boolean existsByTitle(String title);
}
```

**Free Methods from JpaRepository:**
- `save(task)` - Insert or update
- `findById(id)` - Get by ID
- `findAll()` - Get all records
- `deleteById(id)` - Delete by ID
- `count()` - Count all records
- `existsById(id)` - Check if exists

**Custom Query Methods:**
Spring generates SQL automatically based on method names!

| Method Name | Generated SQL |
|-------------|---------------|
| `findByStatus(status)` | `SELECT * FROM tasks WHERE status = ?` |
| `findByTitleContainingIgnoreCase(keyword)` | `SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%keyword%')` |
| `findAllByOrderByDueDateAsc()` | `SELECT * FROM tasks ORDER BY due_date ASC` |
| `existsByTitle(title)` | `SELECT COUNT(*) > 0 FROM tasks WHERE title = ?` |

**Magic Naming Convention:**
- `findBy` → SELECT WHERE
- `Containing` → LIKE '%keyword%'
- `IgnoreCase` → Case-insensitive comparison
- `OrderBy` → ORDER BY clause
- `Asc` / `Desc` → Ascending/Descending

---

### 3️⃣ Service Layer (Business Logic)

**Location:** `src/main/java/.../service/`

**Purpose:** Contains business logic and acts as a middle layer between Controller and Repository.

#### TaskService.java
```java
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    // Business logic methods...
}
```

**Available Methods:**
- `createTask(task)` - Create new task
- `getAllTasks()` - Get all tasks
- `getTaskById(id)` - Get specific task
- `updateTask(id, taskDetails)` - Update existing task
- `deleteTask(id)` - Delete task
- `getTasksByStatus(status)` - Filter by status
- `searchTasksByTitle(keyword)` - Search by keyword
- `markTaskAsComplete(id)` - Quick complete action

**Key Concept: Dependency Injection**
```java
@Autowired
public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
}
```
- Spring automatically injects `TaskRepository` into the constructor
- You never write `new TaskRepository()` - Spring manages object creation
- This is a core Spring concept!

---

### 4️⃣ Controller Layer (REST API) - To Be Added Next!

**Location:** `src/main/java/.../controller/`

**Purpose:** Exposes REST API endpoints for HTTP requests.

**Planned Endpoints:**
```
GET    /api/tasks              → Get all tasks
GET    /api/tasks/{id}         → Get task by ID
POST   /api/tasks              → Create new task
PUT    /api/tasks/{id}         → Update task
DELETE /api/tasks/{id}         → Delete task
GET    /api/tasks/status/{status} → Filter by status
GET    /api/tasks/search?keyword={keyword} → Search tasks
PATCH  /api/tasks/{id}/complete → Mark as complete
```

---

## 🔑 Key Concepts

### 1. Lombok Annotations

**Problem:** Traditional Java requires lots of boilerplate code.

**Traditional Way (50+ lines):**
```java
public class Task {
    private Long id;
    private String title;
    
    public Task() {}
    
    public Task(Long id, String title) {
        this.id = id;
        this.title = title;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    // ... many more lines
}
```

**With Lombok (5 lines):**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long id;
    private String title;
}
```

**What Lombok Generates:**
- `@Data` → getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` → Empty constructor: `new Task()`
- `@AllArgsConstructor` → Full constructor: `new Task(id, title)`

**Usage:**
```java
Task task = new Task();
task.setTitle("Buy groceries");     // Lombok-generated setter
String title = task.getTitle();      // Lombok-generated getter
System.out.println(task);            // Lombok-generated toString
```

---

### 2. JPA Annotations

**Purpose:** Map Java classes to database tables.

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Entity` | Mark class as DB table | `@Entity` |
| `@Table` | Specify table name | `@Table(name = "tasks")` |
| `@Id` | Primary key | `@Id` |
| `@GeneratedValue` | Auto-increment ID | `@GeneratedValue(strategy = GenerationType.IDENTITY)` |
| `@Column` | Column configuration | `@Column(nullable = false, length = 1000)` |
| `@Enumerated` | Store enum in DB | `@Enumerated(EnumType.STRING)` |
| `@PrePersist` | Run before insert | Sets createdAt timestamp |
| `@PreUpdate` | Run before update | Sets updatedAt timestamp |

---

### 3. Spring Data JPA Query Methods

**How it works:**
Spring parses method names and generates SQL queries automatically!

**Examples:**

```java
// Find by exact match
findByTitle(String title)
→ SELECT * FROM tasks WHERE title = ?

// Find by partial match (case-insensitive)
findByTitleContainingIgnoreCase(String keyword)
→ SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%keyword%')

// Find by status and sort
findByStatusOrderByDueDateAsc(TaskStatus status)
→ SELECT * FROM tasks WHERE status = ? ORDER BY due_date ASC

// Check existence
existsByTitle(String title)
→ SELECT COUNT(*) > 0 FROM tasks WHERE title = ?

// Find between dates
findByDueDateBetween(LocalDateTime start, LocalDateTime end)
→ SELECT * FROM tasks WHERE due_date BETWEEN ? AND ?
```

**Common Keywords:**
- `findBy` - Base query
- `And` / `Or` - Logical operators
- `LessThan` / `GreaterThan` - Comparisons
- `Containing` / `StartingWith` / `EndingWith` - String matching
- `IgnoreCase` - Case-insensitive
- `OrderBy` + field + `Asc`/`Desc` - Sorting

---

### 4. Dependency Injection (Spring Core)

**What is it?**
Instead of creating objects yourself with `new`, Spring creates and manages them for you.

**Traditional Way:**
```java
public class TaskService {
    private TaskRepository taskRepository = new TaskRepository(); // Manual creation
}
```

**Spring Way (Dependency Injection):**
```java
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    
    @Autowired  // Spring automatically provides TaskRepository
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
```

**Benefits:**
- ✅ Easier testing (can inject mock objects)
- ✅ Loose coupling (classes don't depend on concrete implementations)
- ✅ Spring manages object lifecycle
- ✅ Singleton pattern by default (one instance shared)

---

## ⚙️ Configuration

### application.properties

```properties
# Application name
spring.application.name=task-management-api

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console (for viewing database in browser)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Server port
server.port=8080
```

**What Each Setting Does:**

| Setting | Purpose |
|---------|---------|
| `spring.datasource.url` | Database connection string (in-memory H2) |
| `spring.jpa.hibernate.ddl-auto=update` | Auto-create/update tables from entities |
| `spring.jpa.show-sql=true` | Print SQL queries to console (great for learning!) |
| `spring.h2.console.enabled=true` | Enable web UI to view database |
| `server.port=8080` | Application runs on http://localhost:8080 |

---

## 📦 Maven Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Web - for REST APIs -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Data JPA - for database operations -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- H2 Database - in-memory database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok - reduces boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🎯 Next Steps

### Immediate (Controller Layer)
1. Create `TaskController.java`
2. Add REST API endpoints:
   - `@GetMapping` - Retrieve data
   - `@PostMapping` - Create data
   - `@PutMapping` - Update data
   - `@DeleteMapping` - Delete data
3. Test API with Postman or browser

### Future Enhancements
- Add input validation (`@Valid`, `@NotNull`, etc.)
- Add custom exception handling
- Add pagination for large datasets
- Add authentication & authorization (Spring Security)
- Switch to PostgreSQL for production
- Add unit and integration tests
- Add API documentation (Swagger/OpenAPI)
- Add logging
- Deploy to cloud (AWS, Azure, Heroku)

---

## 📚 Learning Resources

**Official Documentation:**
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Lombok: https://projectlombok.org/

**Key Concepts to Study:**
1. **Spring Core** - Dependency Injection, IoC Container
2. **Spring Boot** - Auto-configuration, Starters
3. **JPA/Hibernate** - ORM concepts, Entity relationships
4. **REST API** - HTTP methods, Status codes, JSON
5. **Maven** - Dependency management, Build lifecycle

---

## 🤝 Contributing

This is a learning project. Feel free to:
- Add new features
- Improve code quality
- Add tests
- Enhance documentation

---

## 📝 Notes

**Why H2 Database?**
- Perfect for learning and development
- No installation required
- Runs in memory
- Easy to reset (just restart the app)
- For production, switch to PostgreSQL, MySQL, etc.

**Why Lombok?**
- Reduces boilerplate by 70%+
- Industry standard
- Makes code more readable
- Easy to maintain

**Why Layered Architecture?**
- **Separation of concerns** - Each layer has one responsibility
- **Testability** - Easy to test each layer independently
- **Maintainability** - Changes in one layer don't affect others
- **Industry standard** - Used in most enterprise applications

---

**Created by:** Chanan  
**Date:** February 2026  
**Purpose:** Learning Spring Boot ecosystem for Capitolis position

---

**Ready to continue? Next: Creating the Controller layer for REST API endpoints!** 🚀
