import data.*;
import service.*;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File testFile;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            testFile = File.createTempFile("tasks", ".csv");
            Files.write(testFile.toPath(), List.of("id,type,name,status,description,duration,startTime,epic"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTaskManager(testFile);
    }

    @Test
    void shouldSaveAndLoadAllTasks() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(manager.getAllTasks().size(), loaded.getAllTasks().size(), "Количество задач не совпадает");
        assertEquals(manager.getAllEpics().size(), loaded.getAllEpics().size(), "Количество эпиков не совпадает");
        assertEquals(manager.getAllSubtasks().size(), loaded.getAllSubtasks().size(), "Количество сабтасков не совпадает");
    }

    @Test
    void shouldRestoreNextTaskIdCorrectly() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        Task newTask = new Task("Task1", "Task1 description");
        loaded.addTask(newTask);
        assertTrue(newTask.getId() > 0, "Id должен корректно устанавливаться после загрузки");
    }

    @Test
    void shouldUpdateFileAfterRemovingTask() {
        int taskId = manager.getAllTasks().get(0).getId();
        manager.removeTaskById(taskId);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        assertNull(loaded.getTaskById(taskId), "Задача должна быть удалена из файла");
    }

    @Test
    void shouldUpdateFileAfterUpdatingTask() {
        Task t = manager.getAllTasks().get(0);
        t.setName("UpdatedName");
        t.setDescription("UpdatedDesc");
        t.setStatus(Status.DONE);
        manager.updateTask(t);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(testFile);
        Task restored = loaded.getTaskById(t.getId());

        assertEquals("UpdatedName", restored.getName());
        assertEquals("UpdatedDesc", restored.getDescription());
        assertEquals(Status.DONE, restored.getStatus());
    }
}
