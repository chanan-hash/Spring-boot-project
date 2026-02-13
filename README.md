# Task Management API

A RESTful API for managing tasks built with Spring Boot, demonstrating modern Java enterprise development practices including **Strategy Design Pattern** for browser auto-opening.

## 📋 Table of Contents
- [Technologies Used](#technologies-used)
- [Project Architecture](#project-architecture)
- [New Features](#new-features)
- [Setup Instructions](#setup-instructions)
- [Project Structure](#project-structure)
- [Layer Explanations](#layer-explanations)
- [Design Patterns Implemented](#design-patterns-implemented)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Testing the API](#testing-the-api)
- [Next Steps](#next-steps)

---

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming language |
| **Maven** | - | Dependency management & build tool |
| **Spring Boot** | 4.0.2 | Framework that simplifies Spring development |
| **Spring Data JPA** | - | Database operations without writing SQL |
| **Spring Core** | - | Dependency Injection & application context |
| **H2 Database** | - | In-memory database for development |
| **Lombok** | - | Reduces boilerplate code |

---

## 🏗️ Project Architecture

We follow a **4-Layer Architecture** pattern with **Strategy Design Pattern** for configuration:

```
┌─────────────────────────────────────────┐
│         MODEL LAYER                     │
│  (Database structure & entities)        │
│  - Task.java                            │
│  - TaskStatus.java (enum)               │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│      REPOSITORY LAYER                   │
│  (Database operations - CRUD)           │
│  - TaskRepository.java                  │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│       SERVICE LAYER                     │
│  (Business logic)                       │
│  - TaskService.java                     │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│       CONTROLLER LAYER                  │
│  (REST API endpoints)                   │
│  - TaskController.java                  │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│    CONFIGURATION LAYER (Strategy)       │
│  (Browser auto-open strategies)         │
│  - UrlStrategy (interface)              │
│  - ApiEndpointStrategy                  │
│  - H2ConsoleStrategy                    │
│  - HomePageStrategy                     │
│  - BrowserOpener (CommandLineRunner)    │
└─────────────────────────────────────────┘
```

---

## 🎯 New Features

### 1. **Strategy Design Pattern for Browser Auto-Open**

We implemented the **Strategy Pattern** to allow flexible browser opening on application startup:

- **Interface**: `UrlStrategy` - defines contract for URL generation
- **Strategies**: 
  - `ApiEndpointStrategy` - opens REST API endpoint
  - `H2ConsoleStrategy` - opens H2 database console
  - `HomePageStrategy` - opens home page
- **Context**: `BrowserOpener` - executes chosen strategy

**Benefits:**
- ✅ Easy to add new strategies (just implement interface)
- ✅ Choose strategy at runtime via interactive menu
- ✅ Configuration-driven via `application.properties`
- ✅ Follows Open/Closed Principle

### 2. **Interactive Startup Menu**

When the application starts, you can choose which pages to open:

```
============================================================
🌐 === Browser Auto-Open Options ===
============================================================
Select which page to open:
1. API Endpoint - http://localhost:8080/api/tasks
2. H2 Database Console - http://localhost:8080/h2-console
3. Home Page - http://localhost:8080
0. Continue without opening browser
9. Exit application
============================================================

Your choice: _
```

**Features:**
- Open multiple pages in one session
- Menu reappears after each selection
- Option to skip and continue
- Option to gracefully shutdown the application
- Cross-platform browser opening (Windows, macOS, Linux)

### 3. **Dynamic Port Configuration**

All URLs automatically adjust to the configured port:

```properties
server.port=8080  # Change this, everything updates!
```

No need to change URLs in multiple places - the Strategy Pattern handles it!

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

4. **Configure Browser Auto-Open** (Optional)
   
   Edit `src/main/resources/application.properties`:
   
   ```properties
   # Enable/disable auto-open
   app.browser.auto-open=true
   
   # Interactive menu (true) or automatic (false)
   app.browser.interactive=true
   
   # If interactive=false, choose default strategy
   app.browser.strategy=apiEndpoint
   ```

5. **Run the application**
   - Open `TaskManagementApiApplication.java`
   - Click the green play button
   - Or run: `mvn spring-boot:run`
   - **Choose from the interactive menu!**

6. **Access the API**
   - API: http://localhost:8080/api/tasks
   - H2 Console: http://localhost:8080/h2-console
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
│   │   │   ├── config/                      # Configuration & Strategies
│   │   │   │   ├── UrlStrategy.java         # Strategy interface
│   │   │   │   ├── ApiEndpointStrategy.java # API endpoint strategy
│   │   │   │   ├── H2ConsoleStrategy.java   # H2 console strategy
│   │   │   │   └── HomePageStrategy.java    # Home page strategy
│   │   │   ├── controller/                  # REST API layer
│   │   │   │   └── TaskController.java      # API endpoints
│   │   │   ├── model/                       # Entity classes
│   │   │   │   ├── Task.java                # Task entity
│   │   │   │   └── TaskStatus.java          # Enum for statuses
│   │   │   ├── repository/                  # Data access layer
│   │   │   │   └── TaskRepository.java      # Database operations
│   │   │   ├── service/                     # Business logic layer
│   │   │   │   └── TaskService.java         # Task business logic
│   │   │   ├── BrowserOpener.java           # CommandLineRunner for browser
│   │   │   └── TaskManagementApiApplication.java  # Main application
│   │   └── resources/
│   │       └── application.properties       # Configuration file
│   └── test/                                # Test classes
├── pom.xml                                  # Maven dependencies
└── README.md                                # This file
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
    public Task createTask(Task task) { ... }
    public List<Task> getAllTasks() { ... }
    public Task updateTask(Long id, Task taskDetails) { ... }
    // ... more methods
}
```

---

### 4️⃣ Controller Layer (REST API)

**Location:** `src/main/java/.../controller/`

**Purpose:** Exposes REST API endpoints for HTTP requests.

#### TaskController.java
```java
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() { ... }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) { ... }
    
    // ... more endpoints
}
```

---

## 🎨 Design Patterns Implemented

### Strategy Pattern (Browser Auto-Open)

**Problem:** Different developers want different pages to open on startup (API, H2 console, or nothing).

**Solution:** Strategy Pattern allows runtime selection of browser opening behavior.

**Implementation:**

```
           UrlStrategy (Interface)
                  ↑
                  |
    ┌─────────────┼──────────────┐
    |             |              |
ApiEndpoint   H2Console      HomePage
 Strategy      Strategy       Strategy
```

**Usage:**

```properties
# application.properties

# Mode 1: Interactive menu
app.browser.interactive=true

# Mode 2: Automatic with chosen strategy
app.browser.interactive=false
app.browser.strategy=h2DatabaseConsole

# Mode 3: Disabled
app.browser.auto-open=false
```

**Adding a New Strategy:**

1. Create a new class implementing `UrlStrategy`:

```java
@Component("customPage")
public class CustomPageStrategy implements UrlStrategy {
    @Override
    public String getUrl(int port) {
        return "http://localhost:" + port + "/custom";
    }
    
    @Override
    public String getName() {
        return "Custom Page";
    }
}
```

2. It automatically appears in the menu!

---

## ⚙️ Configuration

### application.properties

```properties
# Application name
spring.application.name=task-management-api

# Server port (dynamic - change easily here!)
server.port=8080

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console (for viewing database in browser)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Browser Auto-Open Configuration
app.browser.auto-open=true
app.browser.interactive=true
# Options for strategy (when interactive=false): apiEndpoint, h2DatabaseConsole, homePage
app.browser.strategy=apiEndpoint
```

**Configuration Options:**

| Property | Values | Description |
|----------|--------|-------------|
| `server.port` | Integer (e.g., 8080, 3000) | Server port - everything updates automatically |
| `app.browser.auto-open` | `true` / `false` | Enable/disable browser auto-open |
| `app.browser.interactive` | `true` / `false` | Interactive menu vs automatic |
| `app.browser.strategy` | `apiEndpoint`, `h2DatabaseConsole`, `homePage` | Default strategy (if interactive=false) |
| `spring.jpa.show-sql` | `true` / `false` | Show SQL queries in console (helpful for learning!) |

---

## 📡 API Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/tasks` | Get all tasks | None | List of tasks |
| GET | `/api/tasks/{id}` | Get task by ID | None | Single task or 404 |
| POST | `/api/tasks` | Create new task | Task JSON | Created task (201) |
| PUT | `/api/tasks/{id}` | Update task | Task JSON | Updated task or 404 |
| DELETE | `/api/tasks/{id}` | Delete task | None | 204 No Content |
| GET | `/api/tasks/status/{status}` | Filter by status | None | List of tasks |
| GET | `/api/tasks/search?keyword=xyz` | Search tasks | None | List of tasks |
| PATCH | `/api/tasks/{id}/complete` | Mark complete | None | Updated task or 404 |

---

## 🧪 Testing the API

### Method 1: Browser (GET requests only)

**Get all tasks:**
```
http://localhost:8080/api/tasks
```

**Get task by ID:**
```
http://localhost:8080/api/tasks/1
```

**Filter by status:**
```
http://localhost:8080/api/tasks/status/TODO
```

**Search by keyword:**
```
http://localhost:8080/api/tasks/search?keyword=meeting
```

---

### Method 2: Using curl (Command Line)

**Create a new task:**
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete Spring Boot tutorial",
    "description": "Learn all layers of Spring Boot",
    "status": "IN_PROGRESS",
    "dueDate": "2026-02-20T10:00:00"
  }'
```

**Get all tasks:**
```bash
curl http://localhost:8080/api/tasks
```

**Update a task:**
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete Spring Boot tutorial - UPDATED",
    "description": "Learn all layers including Strategy Pattern",
    "status": "DONE",
    "dueDate": "2026-02-20T10:00:00"
  }'
```

**Delete a task:**
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

**Mark task as complete:**
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/complete
```

---

### Method 3: Using Postman

1. **Download Postman**: https://www.postman.com/downloads/
2. **Create a new request**
3. **Set the method** (GET, POST, PUT, DELETE, PATCH)
4. **Set the URL**: `http://localhost:8080/api/tasks`
5. **For POST/PUT**: 
   - Go to "Body" tab
   - Select "raw"
   - Select "JSON" from dropdown
   - Paste JSON:
   ```json
   {
     "title": "My First Task",
     "description": "Testing the API",
     "status": "TODO",
     "dueDate": "2026-02-15T12:00:00"
   }
   ```
6. **Click Send**

---

### Method 4: H2 Database Console

1. **Open**: http://localhost:8080/h2-console
2. **Login with:**
   - JDBC URL: `jdbc:h2:mem:taskdb`
   - Username: `sa`
   - Password: (leave empty)
3. **Run SQL queries:**
   ```sql
   -- See all tasks
   SELECT * FROM tasks;
   
   -- See only TODO tasks
   SELECT * FROM tasks WHERE status = 'TODO';
   
   -- Count tasks
   SELECT COUNT(*) FROM tasks;
   ```

---

## 🎯 Next Steps

### Immediate
- [x] Project setup with Spring Boot
- [x] Create all layers (Model, Repository, Service, Controller)
- [x] Implement Strategy Pattern for browser auto-open
- [x] Configure interactive startup menu
- [ ] **Test API endpoints with Postman**
- [ ] **Create tasks and view in H2 console**

### Future Enhancements
- [ ] Add input validation (`@Valid`, `@NotNull`, etc.)
- [ ] Add custom exception handling
- [ ] Add pagination for large datasets
- [ ] Add authentication & authorization (Spring Security)
- [ ] Switch to PostgreSQL for production
- [ ] Add comprehensive unit and integration tests
- [ ] Add API documentation (Swagger/OpenAPI)
- [ ] Add structured logging
- [ ] Containerize with Docker
- [ ] Deploy to cloud (AWS, Azure, Heroku)

---

## 🔑 Key Concepts Learned

### 1. Lombok Annotations
- `@Data` → getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` → Empty constructor
- `@AllArgsConstructor` → Full constructor

### 2. JPA Annotations
- `@Entity` → Mark class as database table
- `@Id` + `@GeneratedValue` → Auto-increment primary key
- `@Column` → Column configuration
- `@Enumerated` → Store enum in database

### 3. Spring Annotations
- `@RestController` → REST API controller
- `@Service` → Business logic component
- `@Repository` → Data access component
- `@Component` → Generic Spring-managed component
- `@Autowired` → Dependency injection
- `@Value` → Inject property values

### 4. HTTP Method Annotations
- `@GetMapping` → Retrieve data
- `@PostMapping` → Create data
- `@PutMapping` → Update data
- `@DeleteMapping` → Delete data
- `@PatchMapping` → Partial update

### 5. Design Patterns
- **Strategy Pattern** → Runtime selection of algorithms
- **Dependency Injection** → Inversion of Control
- **Layered Architecture** → Separation of concerns
- **Repository Pattern** → Data access abstraction

---

## 📚 Learning Resources

**Official Documentation:**
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Lombok: https://projectlombok.org/

**Design Patterns:**
- Strategy Pattern: https://refactoring.guru/design-patterns/strategy
- Dependency Injection: https://spring.io/guides/gs/spring-boot/

---

## 🤝 Contributing

This is a learning project created to understand:
- Spring Boot ecosystem
- RESTful API design
- JPA and database operations
- Design patterns in practice
- Enterprise Java development

Feel free to:
- Add new features
- Improve code quality
- Add tests
- Enhance documentation

---

## 📝 Notes

### Why H2 Database?
- Perfect for learning and development
- No installation required
- Runs in memory
- Easy to reset (just restart the app)
- Visual console for inspecting data
- For production, switch to PostgreSQL, MySQL, etc.

### Why Lombok?
- Reduces boilerplate by 70%+
- Industry standard
- Makes code more readable
- Easy to maintain
- Compile-time code generation

### Why Layered Architecture?
- **Separation of concerns** - Each layer has one responsibility
- **Testability** - Easy to test each layer independently
- **Maintainability** - Changes in one layer don't affect others
- **Industry standard** - Used in most enterprise applications

### Why Strategy Pattern?
- **Flexibility** - Easy to add new strategies without modifying existing code
- **Runtime selection** - Choose behavior dynamically
- **Configuration-driven** - Change behavior via properties file
- **Open/Closed Principle** - Open for extension, closed for modification

---

## 🎓 Learning Outcomes

By building this project, you've learned:

✅ **Spring Boot** - Auto-configuration, starters, embedded server  
✅ **Spring Core** - Dependency Injection, IoC Container, component scanning  
✅ **Spring Data JPA** - Repository pattern, query methods, database operations  
✅ **JPA/Hibernate** - ORM concepts, entity mapping, automatic table creation  
✅ **REST API Design** - HTTP methods, status codes, JSON serialization  
✅ **Maven** - Dependency management, build lifecycle  
✅ **Lombok** - Annotation processors, boilerplate reduction  
✅ **Design Patterns** - Strategy pattern implementation  
✅ **Enterprise Architecture** - Layered architecture, separation of concerns  

---

**Created by:** Chanan  
**Date:** February 2026  
**Purpose:** Learning Spring Boot ecosystem for Capitolis position  
**Special Features:** Strategy Pattern for browser auto-opening with interactive menu

---

## 🚀 Quick Start Guide

```bash
# 1. Clone the repository
git clone <your-repo-url>

# 2. Navigate to project directory
cd task-management-api

# 3. Run the application
mvn spring-boot:run

# 4. Choose from the interactive menu
# → Select option 1 (API Endpoint) or 2 (H2 Console)

# 5. Test the API
curl http://localhost:8080/api/tasks

# 6. Create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"My Task","status":"TODO"}'

# 7. View in H2 Console
# Open: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:taskdb
# Username: sa
# Password: (empty)
```

**You're ready to start coding!** 🎯
