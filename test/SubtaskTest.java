import data.Subtask;
import data.Status;
import data.Epic;
import service.TaskManager;
import service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SubtaskTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void subtaskCreationTest() {
        Subtask subtask = new Subtask("Test subtaskCreationTest", "Test subtaskCreationTest description", 5);

        assertEquals("Test subtaskCreationTest", subtask.getName());
        assertEquals("Test subtaskCreationTest description", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(5, subtask.getEpicId(), "ID эпика должен быть равен переданному");
    }

    @Test
    void setEpicIdTest() {
        Subtask subtask = new Subtask("Test setEpicIdTest", "Test setEpicIdTest description", 1);
        subtask.setEpicId(10);
        assertEquals(10, subtask.getEpicId(), "ID эпика должен обновляться");
    }

    @Test
    void taskStatusCanBeChanged() {
        Subtask subtask = new Subtask("Test taskStatusCanBeChanged", "Test taskStatusCanBeChanged description", 2);
        subtask.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, subtask.getStatus(), "Статус задачи должен обновляться");
    }

    @Test
    void subtasksAreEqualIfIdsAreEqual() {
        Subtask subtask1 = new Subtask("Test subtasksAreEqualIfIdsAreEqual. Subtask 1", "Test subtasksAreEqualIfIdsAreEqual description 1", 3);
        Subtask subtask2 = new Subtask("Test subtasksAreEqualIfIdsAreEqual. Subtask 2", "Test subtasksAreEqualIfIdsAreEqual description 2", 4);

        subtask1.setId(7);
        subtask2.setId(7);

        assertEquals(subtask1, subtask2, "Подзадачи должны быть равны, если их id совпадает");
    }

    @Test
    void toStringTest() {
        Subtask subtask = new Subtask("Test toStringTest", "Test toStringTest description", 9);
        subtask.setId(7);

        String str = subtask.toString();
        assertTrue(str.contains("Test toStringTest"));
        assertTrue(str.contains("Test toStringTest description"));
        assertTrue(str.contains("7"));
        assertTrue(str.contains("NEW"));
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Test subtaskCannotBeItsOwnEpic", "Test subtaskCannotBeItsOwnEpic description");
        taskManager.addEpic(epic);

        Subtask invalidSubtask = new Subtask("Неверный сабтаск", "Сабтаск не должен быть своим же эпиком", epic.getId());
        invalidSubtask.setId(epic.getId());

        taskManager.addSubtask(invalidSubtask);

        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epic.getId());

        assertTrue(subtasks.isEmpty(), "Сабтаск не должен быть своим же эпиком");
    }
}