import data.Epic;
import data.Status;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

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
}