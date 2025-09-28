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

        Task task1 = new Task("Task1", "Task1 description");
        fileBackedTaskManager.addTask(task1);

        Epic epic1 = new Epic("Epic1", "Epic1 description");
        fileBackedTaskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        fileBackedTaskManager.addSubtask(subtask1);
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
        List<String> lines = Files.readAllLines(testFile.toPath());
        assertEquals("id,type,name,status,description,epic", lines.get(0));

        assertEquals(4, lines.size());

        assertTrue(lines.get(1).contains("Task1"));
        assertTrue(lines.get(2).contains("Epic1"));
        assertTrue(lines.get(3).contains("Subtask1"));
    }

    @Test
    void loadFromFileRestoresAllTasks() {
        List<Task> originalTasks = fileBackedTaskManager.getAllTasks();
        List<Epic> originalEpics = fileBackedTaskManager.getAllEpics();
        List<Subtask> originalSubtasks = fileBackedTaskManager.getAllSubtasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(originalTasks.size(), loadedManager.getAllTasks().size());
        assertEquals(originalEpics.size(), loadedManager.getAllEpics().size());
        assertEquals(originalSubtasks.size(), loadedManager.getAllSubtasks().size());

        assertIterableEquals(originalTasks, loadedManager.getAllTasks(), "Таски не совпадают");
        assertIterableEquals(originalEpics, loadedManager.getAllEpics(), "Эпики не совпадают");
        assertIterableEquals(originalSubtasks, loadedManager.getAllSubtasks(), "Сабтаски не совпадают");
    }

    @Test
    void nextTaskIdAfterLoad() throws IOException {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);

        Task newTask = new Task("Task3", "Task3 description");
        loaded.addTask(newTask);

        assertEquals(4, newTask.getId(), "Новая задача должна получить следующий id после загрузки");
    }

    @Test
    void removeAllAndByIdUpdatesFile() throws IOException {
        fileBackedTaskManager.removeTaskById(fileBackedTaskManager.getAllTasks().get(0).getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        assertEquals(0, loaded.getAllTasks().size(), "После удаления задача должна исчезнуть");

        fileBackedTaskManager.removeAllTasks();
        loaded = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loaded.getAllTasks().isEmpty(), "После removeAllTasks файл должен быть пуст");
    }


    @Test
    void updateTasksUpdatesFile() {
        Task task = fileBackedTaskManager.getAllTasks().get(0);

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