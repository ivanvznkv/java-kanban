import data.Task;
import data.Status;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void taskStatusCanBeChanged() {
        Task task = new Task("Test taskStatusCanBeChanged", "Test taskStatusCanBeChanged description");
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus(), "Статус задачи должен обновляться.");
    }

    @Test
    void tasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task("Test tasksAreEqualIfIdsAreEqual. Task 1", "Test tasksAreEqualIfIdsAreEqual description 1");
        Task task2 = new Task("Test tasksAreEqualIfIdsAreEqual. Task 2", "Test tasksAreEqualIfIdsAreEqual description 2");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны, если их id совпадает.");
    }
}