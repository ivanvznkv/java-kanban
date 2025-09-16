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
    void taskHistoryKeepsOnlyLastViewAndReflectsCurrentState() {
        Task task = new Task("Test taskHistoryKeepsOnlyLastViewAndReflectsCurrentState", "Test taskHistoryKeepsOnlyLastViewAndReflectsCurrentState description");
        task.setStatus(Status.NEW);

        historyManager.add(task);

        task.setDescription("Обновленное описание");
        task.setStatus(Status.IN_PROGRESS);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "В истории должны быть одна запись");

        Task only = history.get(0);

        assertSame(task, only, "В истории должна храниться ссылка на исходный объект only");


        assertEquals("Обновленное описание", only.getDescription(), "Описание в истории должно быть обновленным");
        assertEquals(Status.IN_PROGRESS, only.getStatus(), "Статус в истории должен быть IN_PROGRESS");
    }

    @Test
    void historyCanGrowBeyondTenTasks() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Test historyCanGrowBeyondTenTasks task " + i, "Test historyCanGrowBeyondTenTasks description " + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(15, history.size(), "История должна хранить все добавленные задачи");

        assertEquals(1, history.get(0).getId(), "Первая задача в истории должна быть с id 1");
        assertEquals(15, history.get(history.size() - 1).getId(), "Последняя задача в истории должна быть с id 15");
    }

    @Test
    void repeatedViewingMovesTaskToEnd() {
        Task task1 = new Task("Test repeatedViewingMovesTaskToEnd task1", "Test repeatedViewingMovesTaskToEnd task1 description");
        task1.setId(1);
        Task task2 = new Task("Test repeatedViewingMovesTaskToEnd task2", "Test repeatedViewingMovesTaskToEnd task2 description");
        task2.setId(2);
        Task task3 = new Task("Test repeatedViewingMovesTaskToEnd task3", "Test repeatedViewingMovesTaskToEnd task3 description");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "В истории должно быть 3 задачи");
        assertEquals(task1, history.get(0), "На первом месте должна быть task1");
        assertEquals(task3, history.get(1), "На втором месте должна быть task3");
        assertEquals(task2, history.get(2), "На последнем месте должна быть task2");

    }

    @Test
    void removeByIdRemovesFromHistory() {
        Task task1 = new Task("Test removeByIdRemovesFromHistory task1", "Test removeByIdRemovesFromHistory task1 description");
        task1.setId(1);
        Task task2 = new Task("Test removeByIdRemovesFromHistory task2", "Test removeByIdRemovesFromHistory task2 description");
        task2.setId(2);
        Task task3 = new Task("Test removeByIdRemovesFromHistory task3", "Test removeByIdRemovesFromHistory task3 description");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории должно быть 2 задачи");
        assertEquals(task1, history.get(0), "На первом месте должна быть task1");
        assertEquals(task3, history.get(1), "На втором месте должна быть task3");
        assertFalse(history.contains(task2), "Задача task2 не должна быть в истории");
    }

    @Test
    void addNullIgnored() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна остаться пустой");
    }

    @Test
    void historyReflectsMutableTasks() {
        Task task = new Task("Test historyReflectsMutableTasks task", "Test historyReflectsMutableTasks description");
        task.setId(1);

        historyManager.add(task);
        task.setName("Обновленное имя");

        List<Task> history = historyManager.getHistory();
        assertEquals("Обновленное имя", history.get(0).getName(), "История должна хранить ссылку на задачу, новое имя должно отразиться");
    }
}