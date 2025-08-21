import data.Task;
import data.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void taskHistoryPreservesPreviousVersion() {
        Task task = new Task("Test taskHistoryPreservesPreviousVersion", "Test taskHistoryPreservesPreviousVersion description");
        task.setStatus(Status.NEW);

        historyManager.add(task);

        task.setDescription("Обновленное описание");
        task.setStatus(Status.IN_PROGRESS);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "В истории должны быть две задачи");

        Task firstVersion = history.get(0);
        Task secondVersion = history.get(1);

        assertEquals("Test taskHistoryPreservesPreviousVersion description", firstVersion.getDescription(), "Описание первой версии должно быть исходным");
        assertEquals(Status.NEW, firstVersion.getStatus(), "Статус первой версии должен быть NEW");

        assertEquals("Обновленное описание", secondVersion.getDescription(), "Описание второй версии должно быть обновленным");
        assertEquals(Status.IN_PROGRESS, secondVersion.getStatus(), "Статус второй версии должен быть IN_PROGRESS");
    }
}