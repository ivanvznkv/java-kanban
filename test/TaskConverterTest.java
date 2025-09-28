import service.TaskConverter;
import data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskConverterTest {

    private Task task;
    private Epic epic;
    private Epic epicForSubtask;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        task = new Task("Task1", "Task1 description", TaskType.TASK);
        task.setId(1);

        epic = new Epic("Epic1", "Epic1 description");
        epic.setId(2);

        epicForSubtask = new Epic("EpicForSubtask", "Epic description");
        epicForSubtask.setId(10);
        subtask = new Subtask("Subtask1", "Subtask1 description", epicForSubtask.getId());
        subtask.setId(3);
    }

    @Test
    void testToStringTask() {
        String str = TaskConverter.toString(task);
        assertTrue(str.contains("1"));
        assertTrue(str.contains(TaskType.TASK.toString()));
        assertTrue(str.contains("Task1"));
        assertTrue(str.contains("Task1 description"));
        assertTrue(str.endsWith(","));
    }

    @Test
    void testToStringEpic() {
        String str = TaskConverter.toString(epic);
        assertTrue(str.contains("2"));
        assertTrue(str.contains(TaskType.EPIC.toString()));
        assertTrue(str.contains("Epic1"));
        assertTrue(str.contains("Epic1 description"));
        assertTrue(str.endsWith(","));
    }

    @Test
    void testToStringSubtask() {
        String str = TaskConverter.toString(subtask);
        assertTrue(str.contains("3"));
        assertTrue(str.contains(TaskType.SUBTASK.toString()));
        assertTrue(str.contains("Subtask1"));
        assertTrue(str.contains("Subtask1 description"));
        assertTrue(str.endsWith("10"));
    }

    @Test
    void testFromStringTask() {
        String csv = "1,TASK,Task1,NEW,Task1 description,";
        Task task = TaskConverter.fromString(csv);

        assertEquals(1, task.getId());
        assertEquals(TaskType.TASK, task.getType());
        assertEquals("Task1", task.getName());
        assertEquals("Task1 description", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void testFromStringEpic() {
        String csv = "2,EPIC,Epic1,IN_PROGRESS,Epic1 description,";
        Epic epic = (Epic) TaskConverter.fromString(csv);

        assertEquals(2, epic.getId());
        assertEquals(TaskType.EPIC, epic.getType());
        assertEquals("Epic1", epic.getName());
        assertEquals("Epic1 description", epic.getDescription());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testFromStringSubtask() {
        String csv = "3,SUBTASK,Subtask1,DONE,Subtask1 description,10";
        Subtask subtask = (Subtask) TaskConverter.fromString(csv);

        assertEquals(3, subtask.getId());
        assertEquals(TaskType.SUBTASK, subtask.getType());
        assertEquals("Subtask1", subtask.getName());
        assertEquals("Subtask1 description", subtask.getDescription());
        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(10, subtask.getEpicId());
    }
}
