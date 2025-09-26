import data.Task;
import data.Epic;
import data.Subtask;
import data.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private File testFile;

    @BeforeEach
    public void setUp() throws IOException {
        testFile = File.createTempFile("tasks", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(testFile);
    }

    @Test
    void saveEmptyManagerToFile() throws IOException {
        fileBackedTaskManager.removeAllTasks();
        fileBackedTaskManager.removeAllEpic();
        fileBackedTaskManager.removeAllSubtask();

        List<String> lines = Files.readAllLines(testFile.toPath());
        assertEquals(1, lines.size());
        assertEquals("id,type,name,status,description,epic", lines.get(0));
    }

    @Test
    void saveMultipleTasksToFile() throws IOException {
        Task task1 = new Task("Task1", "Task1 description");
        fileBackedTaskManager.addTask(task1);

        Epic epic1 = new Epic("Epic1", "Epic1 description");
        fileBackedTaskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        fileBackedTaskManager.addSubtask(subtask1);

        List<String> lines = Files.readAllLines(testFile.toPath());
        assertEquals("id,type,name,status,description,epic", lines.get(0));

        assertEquals(4, lines.size());

        assertTrue(lines.get(1).contains("Task1"));
        assertTrue(lines.get(2).contains("Epic1"));
        assertTrue(lines.get(3).contains("Subtask1"));
    }

    @Test
    void loadFromFileRestoresAllTasks() {
        Task task1 = new Task("Task1", "Task1 description");
        fileBackedTaskManager.addTask(task1);
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        fileBackedTaskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        fileBackedTaskManager.addSubtask(subtask1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());
    }

    @Test
    void nextTaskIdAfterLoad() throws IOException {
        Task task1 = new Task("Task1", "Task1 description");
        Task task2 = new Task("Task2", "Task2 description");
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);

        Task newTask = new Task("Task3", "Task3 description");
        loaded.addTask(newTask);

        assertEquals(3, newTask.getId());
    }

    @Test
    void removeAllAndByIdUpdatesFile() throws IOException {
        Task task = new Task("Task1", "Task1 description");
        fileBackedTaskManager.addTask(task);

        fileBackedTaskManager.removeTaskById(task.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loaded.getAllTasks().isEmpty(), "После удаления задача должна исчезнуть");

        // проверим полное удаление
        Task task2 = new Task("Task2", "Task2 description");
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.removeAllTasks();

        loaded = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loaded.getAllTasks().isEmpty(), "После removeAllTasks файл должен быть пуст");
    }

    @Test
    void updateTasksUpdatesFile() {
        Task task = new Task("Task1", "Task1 description");
        fileBackedTaskManager.addTask(task);

        task.setName("NewName");
        task.setDescription("NewDesc");
        task.setStatus(Status.IN_PROGRESS);
        fileBackedTaskManager.updateTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        Task loadedTask = loaded.getTaskById(task.getId());

        assertNotNull(loadedTask);
        assertEquals("NewName", loadedTask.getName());
        assertEquals("NewDesc", loadedTask.getDescription());
        assertEquals(Status.IN_PROGRESS, loadedTask.getStatus());
    }
}