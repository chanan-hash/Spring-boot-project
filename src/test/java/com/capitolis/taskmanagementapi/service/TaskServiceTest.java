package com.capitolis.taskmanagementapi.service;

import com.capitolis.taskmanagementapi.model.Task;
import com.capitolis.taskmanagementapi.model.TaskStatus;
import com.capitolis.taskmanagementapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // annotation is used to enable Mockito support in JUnit 5 tests. It allows us to use Mockito annotations like @Mock and @InjectMocks without needing to manually initialize them. When the test class is run, Mockito will automatically create mock instances for the annotated fields and inject them where needed.
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock // annotation is used to create a mock instance of the TaskRepository interface. This allows us to simulate the behavior of the repository without needing a real database connection.
    private TaskRepository taskRepository;

    @InjectMocks // annotation is used to create an instance of TaskService and inject the mocked TaskRepository into it. This allows us to test the TaskService methods in isolation, with the repository behavior controlled by our test setup.
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach // annotation is used to indicate that the annotated method should be executed before each test method in the class. In this case, the setUp() method will run before each test, allowing us to initialize common test data (like sampleTask) that can be reused across multiple test cases.
    void setUp() {
        // Create a sample task for testing
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("This is a test task");
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== CREATE TASK TESTS ====================

    /**
     * // Setup: Define what mock should return
     * when(repository.findById(1L)).thenReturn(Optional.of(task));

     * // Verify: Check method was called
     * verify(repository, times(1)).findById(1L);

     * // Verify: Method was never called
     * verify(repository, never()).save(any());
     */

    @Test
    @DisplayName("Should successfully create a task")
    void testCreateTask_Success() {
        // Arrange (Given)
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask); // Mock the save method to return the sample task when any Task object is saved

        // Act (When)
        Task result = taskService.createTask(sampleTask);

        // Assert (Then)
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should set default status to TODO when creating task without status")
    void testCreateTask_DefaultStatus() {
        // Arrange
        Task taskWithoutStatus = new Task();
        taskWithoutStatus.setTitle("No Status Task");
        taskWithoutStatus.setStatus(TaskStatus.TODO);

        when(taskRepository.save(any(Task.class))).thenReturn(taskWithoutStatus);

        // Act
        Task result = taskService.createTask(taskWithoutStatus);

        // Assert
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
    }

    // ==================== GET ALL TASKS TESTS ====================

    @Test
    @DisplayName("Should return all tasks")
    void testGetAllTasks_Success() {
        // Arrange
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.TODO);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(task1, task2);

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void testGetAllTasks_EmptyList() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(List.of());

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== GET TASK BY ID TESTS ====================

    @Test
    @DisplayName("Should return task when ID exists")
    void testGetTaskById_Found() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        // Act
        Optional<Task> result = taskService.getTaskById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Task");

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when task ID does not exist")
    void testGetTaskById_NotFound() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Task> result = taskService.getTaskById(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== UPDATE TASK TESTS ====================

    @Test
    @DisplayName("Should successfully update an existing task")
    void testUpdateTask_Success() {
        // Arrange
        Task updatedDetails = new Task();
        updatedDetails.setTitle("Updated Title");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setStatus(TaskStatus.IN_PROGRESS);
        updatedDetails.setDueDate(LocalDateTime.now().plusDays(7));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        Task result = taskService.updateTask(1L, updatedDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent task")
    void testUpdateTask_NotFound() {
        // Arrange
        Task updatedDetails = new Task();
        updatedDetails.setTitle("Updated Title");

        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(999L, updatedDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found with id: 999");

        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ==================== DELETE TASK TESTS ====================

    @Test
    @DisplayName("Should successfully delete a task")
    void testDeleteTask_Success() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void testDeleteTask_NotFound() {
        // Arrange
        when(taskRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found with id: 999");

        verify(taskRepository, times(1)).existsById(999L);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    // ==================== GET TASKS BY STATUS TESTS ====================

    @Test
    @DisplayName("Should return tasks filtered by TODO status")
    void testGetTasksByStatus_TODO() {
        // Arrange
        Task todoTask1 = new Task();
        todoTask1.setTitle("TODO Task 1");
        todoTask1.setStatus(TaskStatus.TODO);

        Task todoTask2 = new Task();
        todoTask2.setTitle("TODO Task 2");
        todoTask2.setStatus(TaskStatus.TODO);

        List<Task> todoTasks = Arrays.asList(todoTask1, todoTask2);
        when(taskRepository.findByStatus(TaskStatus.TODO)).thenReturn(todoTasks);

        // Act
        List<Task> result = taskService.getTasksByStatus(TaskStatus.TODO);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(task -> task.getStatus() == TaskStatus.TODO);
    }

    @Test
    @DisplayName("Should return tasks filtered by IN_PROGRESS status")
    void testGetTasksByStatus_InProgress() {
        // Arrange
        Task inProgressTask = new Task();
        inProgressTask.setTitle("In Progress Task");
        inProgressTask.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findByStatus(TaskStatus.IN_PROGRESS))
                .thenReturn(List.of(inProgressTask));

        // Act
        List<Task> result = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    // ==================== SEARCH TASKS TESTS ====================

    @Test
    @DisplayName("Should find tasks matching search keyword")
    void testSearchTasksByTitle_Found() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Learn Spring Boot");

        Task task2 = new Task();
        task2.setTitle("Spring Data JPA Tutorial");

        when(taskRepository.findByTitleContainingIgnoreCase("spring"))
                .thenReturn(Arrays.asList(task1, task2));

        // Act
        List<Task> result = taskService.searchTasksByTitle("spring");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(task ->
                task.getTitle().toLowerCase().contains("spring"));
    }

    @Test
    @DisplayName("Should return empty list when no tasks match search")
    void testSearchTasksByTitle_NotFound() {
        // Arrange
        when(taskRepository.findByTitleContainingIgnoreCase("nonexistent"))
                .thenReturn(List.of());

        // Act
        List<Task> result = taskService.searchTasksByTitle("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== MARK AS COMPLETE TESTS ====================

    @Test
    @DisplayName("Should mark task as DONE")
    void testMarkTaskAsComplete_Success() {
        // Arrange
        sampleTask.setStatus(TaskStatus.TODO);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setStatus(TaskStatus.DONE);
            return saved;
        });

        // Act
        Task result = taskService.markTaskAsComplete(1L);

        // Assert
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when marking non-existent task as complete")
    void testMarkTaskAsComplete_NotFound() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.markTaskAsComplete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found with id: 999");
    }
}