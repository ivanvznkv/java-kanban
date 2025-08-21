import data.Epic;
import data.Subtask;
import data.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;
import service.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void epicCreationTest() {
        Epic epic = new Epic("Test epicCreationTest", "Test epicCreationTest description");

        assertEquals("Test epicCreationTest", epic.getName());
        assertEquals("Test epicCreationTest description", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
        assertTrue(epic.getEpicsId().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void addSubtaskIdTest() {
        Epic epic = new Epic("Test addSubtaskIdTest", "Test addSubtaskIdTest description");
        epic.addEpicIds(10);
        epic.addEpicIds(20);

        ArrayList<Integer> ids = epic.getEpicsId();
        assertEquals(2, ids.size());
        assertTrue(ids.contains(10));
        assertTrue(ids.contains(20));
    }

    @Test
    void removeSubtaskIdTest() {
        Epic epic = new Epic("Test removeSubtaskIdTest", "Test removeSubtaskIdTest description");
        epic.addEpicIds(1);
        epic.addEpicIds(2);

        epic.removeSubtaskId(1);
        ArrayList<Integer> ids = epic.getEpicsId();
        assertEquals(1, ids.size());
        assertFalse(ids.contains(1));
        assertTrue(ids.contains(2));

        epic.removeSubtaskId(100);
        assertEquals(1, ids.size());
    }

    @Test
    void epicEqualsByIdTest() {
        Epic epic1 = new Epic("Test epicEqualsByIdTest. Epic 1", "Test epicEqualsByIdTest description 1");
        Epic epic2 = new Epic("Test epicEqualsByIdTest. Epic 2", "Test epicEqualsByIdTest description 2");

        epic1.setId(5);
        epic2.setId(5);

        assertEquals(epic1, epic2, "Эпики должны быть равны по id");
    }

    @Test
    void toStringTest() {
        Epic epic = new Epic("Test toStringTest", "Test toStringTest description");
        epic.setId(7);

        String str = epic.toString();
        assertTrue(str.contains("Test toStringTest"));
        assertTrue(str.contains("Test toStringTest description"));
        assertTrue(str.contains("7"));
        assertTrue(str.contains("NEW"));
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test subtaskCannotBeItsOwnEpic", "Test subtaskCannotBeItsOwnEpic description");
        taskManager.addEpic(epic);

        Subtask invalidSubtask = new Subtask("Неверный сабтаск", "Эпик не существует", epic.getId() + 1);

        taskManager.addSubtask(invalidSubtask);

        assertTrue(taskManager.getAllSubtasks().isEmpty(),
                "Сабтаск не должен быть добавлен, если привязан к несуществующему эпику");
    }
}