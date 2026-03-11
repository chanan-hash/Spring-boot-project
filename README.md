# Task Management API

A simple REST API for managing tasks, built with Spring Boot. Features an interactive console menu and web interface.

---

## 🛠️ What's Inside

**Technologies:**
- **Java 21** - Programming language
- **Spring Boot** - Framework for building the app
- **Spring Data JPA** - Handles database operations (no SQL needed!)
- **H2 Database** - Stores your tasks (saved to disk)
- **Maven** - Manages dependencies

**What You Can Do:**
- ✅ Create, view, update, and delete tasks
- ✅ Mark tasks as TODO, IN_PROGRESS, or DONE
- ✅ Search and filter tasks
- ✅ Use console menu OR REST API
- ✅ View tasks in database console

---

## 🚀 How to Run

### 1. Clone the Project
```bash
git clone <your-repo-url>
cd task-management-api
```

### 2. Run the Application
```bash
mvn spring-boot:run
```

**Or in IntelliJ:**
- Open `TaskManagementApiApplication.java`
- Click the green play button ▶️

### 3. Choose What Opens
You'll see a menu to open different pages:
```
1. API Endpoint - http://localhost:8080/api/tasks
2. H2 Database Console - http://localhost:8080/h2-console
3. Home Page - http://localhost:8080
0. Continue
```

Choose option **1** or **2**, or press **0** to continue.

---

## 📝 Using the Console Menu

After startup, you'll see:

```
📋 TASK MANAGEMENT MENU
1. 📋 View All Tasks
2. ➕ Add New Task
3. ✏️ Update Task
4. 🗑️ Delete Task
5. ✅ Mark Task as Complete
6. 🔍 Search Tasks
7. 🎯 Filter by Status
8. 🌐 Browser Menu
0. Exit
```

**To add a task:**
1. Press `2`
2. Enter title: `Learn Spring Boot`
3. Enter description: `Complete the tutorial`
4. Choose status: `2` (IN_PROGRESS)
5. Enter due date or press Enter to skip

**To view tasks:**
- Press `1` to see all tasks

**To switch menus:**
- Press `8` to go back to browser menu
- Press `0` to exit console (API keeps running)

---

## 🌐 Using the Web Interface

### View All Tasks in Browser
Open: **http://localhost:8080/api/tasks**

You'll see your tasks as JSON:
```json
[
  {
    "id": 1,
    "title": "Learn Spring Boot",
    "description": "Complete the tutorial",
    "status": "IN_PROGRESS",
    "dueDate": "2026-02-20T10:00:00",
    "createdAt": "2026-02-15T10:30:00"
  }
]
```

### View Database
1. Open: **http://localhost:8080/h2-console**
2. Login with:
    - JDBC URL: `jdbc:h2:file:./data/taskdb`
    - Username: `sa`
    - Password: (leave empty)
3. Click **Connect**
4. Run query: `SELECT * FROM tasks;`

---

## 🧪 Running Unit Tests

```bash
mvn test
```

**Or in IntelliJ:**
- Right-click on `src/test/java` → **Run 'All Tests'**

All 16 tests should pass! ✅

---

## ⚙️ Configuration

**Change port:**
Edit `src/main/resources/application.properties`:
```properties
server.port=8081  # Change from 8080 to 8081
```

**Disable auto-open menus:**
```properties
app.browser.auto-open=false
app.console.task-manager.enabled=false
```

---

## 📂 Where is Data Saved?

Your tasks are saved in: `./data/taskdb.mv.db`

**To reset everything:**
1. Stop the app
2. Delete the `data` folder
3. Restart - fresh database!

---

## 🎯 API Endpoints Quick Reference

| Method | URL | What it Does |
|--------|-----|--------------|
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/1` | Get task #1 |
| POST | `/api/tasks` | Create new task |
| PUT | `/api/tasks/1` | Update task #1 |
| DELETE | `/api/tasks/1` | Delete task #1 |
| PATCH | `/api/tasks/1/complete` | Mark task #1 as done |
| GET | `/api/tasks/status/TODO` | Get only TODO tasks |
| GET | `/api/tasks/search?keyword=spring` | Search for "spring" |

---

## 📚 Want to Learn More?

Check out the **[Technical Guide](TECHNICAL_GUIDE.md)** for:
- How the code is organized
- Design patterns used
- Database design
- Testing strategy
- Detailed explanations

---

## 🐛 Common Issues

**Port 8080 already in use:**
```properties
# Change port in application.properties
server.port=8081
```

**Can't connect to H2 Console:**
- JDBC URL must be: `jdbc:h2:file:./data/taskdb`
- Username: `sa`
- Password: (empty)

**Tests show warnings:**
- These are harmless Java 21 warnings
- Tests still pass correctly ✅

---

## 📝 Example Workflow

```bash
# 1. Start the app
mvn spring-boot:run

# 2. Open browser menu, select option 1 (API)
# Browser opens to: http://localhost:8080/api/tasks

# 3. In console menu, press 2 to add a task
Title: Buy groceries
Description: Milk, bread, eggs
Status: 1 (TODO)

# 4. Press 1 to view all tasks
# See your task in the console!

# 5. Refresh browser
# See your task in JSON format!

# 6. Press 8 to open H2 console
# Login and run: SELECT * FROM tasks;
# See your task in the database!
```

---



## 🐳 Running with Docker

### Quick Start

```bash
# Build and run with docker-compose
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

**That's it!** Your app is now running in a container.

---

### Access the Application

Once running, open your browser to:

| Feature | URL |
|---------|-----|
| **Web UI** | http://localhost:8080/ |
| **REST API** | http://localhost:8080/api/tasks |
| **H2 Console** | http://localhost:8080/h2-console |

#### H2 Console Login:
- **JDBC URL:** `jdbc:h2:mem:taskdb`
- **Username:** `sa`
- **Password:** (leave empty)
- Click **"Connect"**

---

### Docker Commands

```bash
# Build the image
docker-compose build

# Start containers
docker-compose up

# Start in background (detached mode)
docker-compose up -d

# Stop containers
docker-compose down

# View logs
docker-compose logs -f

# Rebuild and restart
docker-compose up --build
```

---

### Running the Image Directly

After building, you can also run the image directly:

```bash
# Run the container
docker run -p 8080:8080 task-api:latest

# Or with a custom name
docker run -p 8080:8080 --name my-task-api task-api:latest
```

---

### Push to Docker Hub (Optional)

Share your containerized app:

```bash
# Login to Docker Hub
docker login

# Tag the image
docker tag task-api:latest YOUR_USERNAME/task-api:latest

# Push to Docker Hub
docker push YOUR_USERNAME/task-api:latest
```

**Now anyone can run your app:**
```bash
docker run -p 8080:8080 YOUR_USERNAME/task-api:latest
```

---

### Configuration

**Database Type:**  
The Docker version uses an **in-memory H2 database** by default (data resets on restart).

**To change settings, edit `application.properties`:**

```properties
# Use in-memory database (default for Docker)
spring.datasource.url=jdbc:h2:mem:taskdb

# Or use file-based database (persists data)
spring.datasource.url=jdbc:h2:file:./data/taskdb

# Disable interactive features in Docker
app.browser.auto-open=false
app.console.task-manager.enabled=false

# Allow H2 console access from Docker
spring.h2.console.settings.web-allow-others=true
```

**After changing configuration:**
```bash
docker-compose build
docker-compose up
```

---

### What's Different in Docker?

| Feature | Local Run (JAR) | Docker Run |
|---------|----------------|------------|
| **Console Menu** | ✅ Available | ❌ Not available (use Web UI) |
| **Browser Auto-Open** | ✅ Available | ❌ Disabled |
| **Web UI** | ✅ Available | ✅ Available |
| **REST API** | ✅ Available | ✅ Available |
| **H2 Console** | ✅ Available | ✅ Available |

**In Docker, use the Web UI (http://localhost:8080/) instead of the console menu!**

---

### Troubleshooting

**Can't access the app?**
- Check container is running: `docker ps`
- View logs: `docker-compose logs -f`

**Port already in use?**
- Change port in `docker-compose.yml`:
  ```yaml
  ports:
    - "8081:8080"  # Use 8081 instead
  ```

**H2 Console connection failed?**
- Make sure JDBC URL is: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: (empty)

---

## 👤 Author

**Chanan** - Created for Spring Boot learning (February 2026)

---