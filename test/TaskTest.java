// Сергей, добрый день! Код на ревью)

import data.Task;
import data.Status;
import service.TaskManager;
import service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        assertEquals(Status.NEW, task.getStatus(), "Статус - NEW");

        taskManager.addTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Список задач пустой.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void taskHasIdAfterCreation() {
        Task task = new Task("Test taskHasIdAfterCreation", "Test taskHasIdAfterCreation description");
        assertNotNull(task.getId(), "У задачи появляется id сразу после создания.");
    }

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

    @Test
    void taskToStringTest() {
        Task task = new Task("Test taskToStringTest", "Test taskToStringTest description");
        task.setId(7);

        String str = task.toString();
        assertTrue(str.contains("Test taskToStringTest"));
        assertTrue(str.contains("Test taskToStringTest description"));
        assertTrue(str.contains("7"));
        assertTrue(str.contains("NEW"));
    }
}