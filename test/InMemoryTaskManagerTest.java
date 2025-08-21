import data.Task;
import data.Epic;
import data.Subtask;
import data.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void addAndRetrieveTasksOfDifferentTypes() {
        Task task = new Task("Test addAndRetrieveTasksOfDifferentTypes", "Test addAndRetrieveTasksOfDifferentTypes description");
        taskManager.addTask(task);

        Epic epic = new Epic("Test addAndRetrieveTasksOfDifferentTypes. Epic 1", "Test addAndRetrieveTasksOfDifferentTypes description 1");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test addAndRetrieveTasksOfDifferentTypes. Subtask 1", "Test addAndRetrieveTasksOfDifferentTypes description 1", epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(task, taskManager.getTaskById(task.getId()), "Task не найдена по id");
        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Epic не найден по id");
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Subtask не найден по id");
    }

    @Test
    void noIdConflictBetweenGeneratedAndExplicitIds() {
        Task taskWithId = new Task("Test noIdConflictBetweenGeneratedAndExplicitIds", "Test noIdConflictBetweenGeneratedAndExplicitIds description");
        taskWithId.setId(100);
        taskManager.addTask(taskWithId);

        Task autoTask = new Task("Test noIdConflictBetweenGeneratedAndExplicitIds Task 1", "Задача с произвольным ID");
        taskManager.addTask(autoTask);

        assertNotEquals(taskWithId.getId(), autoTask.getId(), "ID сгенерированной задачи не должен конфликтовать с явным id");
    }

    @Test
    void taskRemainsImmutableWhenAdded() {
        Task task = new Task("Test taskRemainsImmutableWhenAdded", "Test taskRemainsImmutableWhenAdded description");
        task.setStatus(Status.IN_PROGRESS);

        String originalName = task.getName();
        String originalDescription = task.getDescription();
        Status originalStatus = task.getStatus();

        taskManager.addTask(task);

        assertEquals(originalName, task.getName(), "Имя задачи изменилось после добавления");
        assertEquals(originalDescription, task.getDescription(), "Описание задачи изменилось после добавления");
        assertEquals(originalStatus, task.getStatus(), "Статус задачи изменился после добавления");
    }
}