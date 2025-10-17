import data.*;

import java.time.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void taskStatusCanBeChanged() {
        Task task = new Task("Task1", "Task1 description");
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus(), "Статус задачи должен обновляться.");
    }

    @Test
    void tasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task("Task1", "Task1 description");
        Task task2 = new Task("Task2", "Task2 description");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны, если их id совпадает.");
    }

    @Test
    void endTimeIsCalculatedCorrectly() {
        Task task = new Task("Task1", "Task1 description");
        task.setStartTime(LocalDateTime.of(2025, 10, 13, 10, 0));
        task.setDuration(Duration.ofHours(2));

        assertEquals(LocalDateTime.of(2025, 10, 13, 12, 0), task.getEndTime());
    }

    @Test
    void endTimeIsNullIfStartOrDurationIsNull() {
        Task task = new Task("Task1", "Task1 description");
        assertNull(task.getEndTime());

        task.setStartTime(LocalDateTime.now());
        assertNull(task.getEndTime());

        task.setStartTime(null);
        task.setDuration(Duration.ofHours(1));
        assertNull(task.getEndTime());
    }
}
