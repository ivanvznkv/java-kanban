import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.InMemoryHistoryManager;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void getDefaultReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не должен быть null");
        assertTrue(taskManager instanceof InMemoryTaskManager, "TaskManager должен быть экземпляром InMemoryTaskManager");
    }

    @Test
    void getDefaultHistoryReturnsInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не должен быть null");
        assertTrue(historyManager instanceof InMemoryHistoryManager, "HistoryManager должен быть экземпляром InMemoryHistoryManager");
    }
}
