import data.Task;
import data.Epic;
import data.Subtask;
import data.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.util.List;

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
    void subtaskCannotBeItsOwnEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test subtaskCannotBeItsOwnEpic", "Test subtaskCannotBeItsOwnEpic description");
        taskManager.addEpic(epic);

        Subtask invalidSubtask = new Subtask("Неверный сабтаск", "Эпик не существует", epic.getId() + 1);

        taskManager.addSubtask(invalidSubtask);

        assertTrue(taskManager.getAllSubtasks().isEmpty(),
                "Сабтаск не должен быть добавлен, если привязан к несуществующему эпику");
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
    void getAllEpicsReturnsAllEpics() {
        Epic epic1 = new Epic("Test getAllEpicsReturnsAllEpics. Epic 1", "Test getAllEpicsReturnsAllEpics description 1");
        Epic epic2 = new Epic("Test getAllEpicsReturnsAllEpics. Epic 2", "Test getAllEpicsReturnsAllEpics description 2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Список эпиков не должен быть null");
        assertEquals(2, epics.size(), "Должно быть два эпика");
        assertTrue(epics.contains(epic1), "Список должен содержать Epic 1");
        assertTrue(epics.contains(epic2), "Список должен содержать Epic 2");
    }

    @Test
    void getAllSubtasksByEpicIdReturnsOnlyItsSubtasks() {
        Epic epic1 = new Epic("Test getAllSubtasksByEpicIdReturnsOnlyItsSubtasks. Epic 1", "Test getAllSubtasksByEpicIdReturnsOnlyItsSubtasks description 1");
        Epic epic2 = new Epic("Test getAllSubtasksByEpicIdReturnsOnlyItsSubtasks. Epic 2", "Test getAllSubtasksByEpicIdReturnsOnlyItsSubtasks description 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epic2.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Subtask> epic1Subtasks = taskManager.getAllSubtasksByEpicId(epic1.getId());

        assertNotNull(epic1Subtasks, "Список сабтасков не должен быть null");
        assertEquals(2, epic1Subtasks.size(), "У Epic 1 должно быть два сабтаска");
        assertTrue(epic1Subtasks.contains(subtask1), "Список должен содержать Subtask 1");
        assertTrue(epic1Subtasks.contains(subtask2), "Список должен содержать Subtask 2");
        assertFalse(epic1Subtasks.contains(subtask3), "Список не должен содержать сабтаск другого эпика");
    }

    @Test
    void getTaskByIdReturnsCorrectTask() {
        Task task = new Task("Test getTaskByIdReturnsCorrectTask Task 1", "Test getAllSubtasksByEpicIdReturnsOnlyItsSubtasks description");
        taskManager.addTask(task);

        Task retrieved = taskManager.getTaskById(task.getId());

        assertNotNull(retrieved, "getTaskById вернул null для существующей задачи");
        assertEquals(task, retrieved, "getTaskById вернул неправильную задачу");
    }

    @Test
    void getEpicByIdReturnsCorrectEpic() {
        Epic epic = new Epic("Test getEpicByIdReturnsCorrectEpic Epic 1", "Test getEpicByIdReturnsCorrectEpic description");
        taskManager.addEpic(epic);

        Epic retrieved = taskManager.getEpicById(epic.getId());

        assertNotNull(retrieved, "getEpicById вернул null для существующего эпика");
        assertEquals(epic, retrieved, "getEpicById вернул неправильный эпик");
    }

    @Test
    void getSubtaskByIdReturnsCorrectSubtask() {
        Epic epic = new Epic("Test getSubtaskByIdReturnsCorrectSubtask Epic for Subtask", "Test getSubtaskByIdReturnsCorrectSubtask description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.addSubtask(subtask);

        Subtask retrieved = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(retrieved, "getSubtaskById вернул null для существующего сабтаска");
        assertEquals(subtask, retrieved, "getSubtaskById вернул неправильный сабтаск");
    }

    @Test
    void getByIdReturnsNullForNonExistentId() {
        assertNull(taskManager.getTaskById(100), "getTaskById должен возвращать null для несуществующего ID");
        assertNull(taskManager.getEpicById(100), "getEpicById должен возвращать null для несуществующего ID");
        assertNull(taskManager.getSubtaskById(100), "getSubtaskById должен возвращать null для несуществующего ID");
    }

    @Test
    void removeAllTasks() {
        Task task1 = new Task("Test removeAllTasks. Task 1", "Test removeAllTasks description 1");
        Task task2 = new Task("Test removeAllTasks. Task 2", "Test removeAllTasks description 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic = new Epic("Epic 1", "description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "description", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.removeAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Эпики не должны быть удалены");
        assertFalse(taskManager.getAllSubtasks().isEmpty(), "Сабтаски не должны быть удалены");
    }

    @Test
    void removeAllEpicAndSubtasks() {
        Epic epic1 = new Epic("Test removeAllEpicAndSubtasks. Epic 1", "Test removeAllEpicAndSubtasks description 1");
        Epic epic2 = new Epic("Test removeAllEpicAndSubtasks. Epic 2", "Test removeAllEpicAndSubtasks description 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask sub1 = new Subtask("Subtask 1", "description", epic1.getId());
        Subtask sub2 = new Subtask("Subtask 2", "description", epic2.getId());
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        Task task = new Task("Task 1", "description");
        taskManager.addTask(task);

        taskManager.removeAllEpic();

        assertTrue(taskManager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Сабтаски эпиков должны быть удалены");
        assertFalse(taskManager.getAllTasks().isEmpty(), "Задачи не должны быть удалены");
    }

    @Test
    void removeAllSubtaskAndClearsEpicLists() {
        Epic epic = new Epic("Test removeAllSubtaskAndClearsEpicLists. Epic", "Test removeAllSubtaskAndClearsEpicLists description");
        taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Subtask 1", "description", epic.getId());
        Subtask sub2 = new Subtask("Subtask 2", "description", epic.getId());
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        Task task = new Task("Task", "description");
        taskManager.addTask(task);

        taskManager.removeAllSubtask();

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Все сабтаски должны быть удалены");
        assertTrue(taskManager.getAllSubtasksByEpicId(epic.getId()).isEmpty(), "Списки сабтасков в эпиках должны быть очищены");
        assertFalse(taskManager.getAllTasks().isEmpty(), "Задачи не должны быть удалены");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Эпик не должен быть удален");
    }

    @Test
    void taskUpdatesCorrectly() {
        Task task = new Task("Test taskUpdatesCorrectly Task", "Test taskUpdatesCorrectly description");
        taskManager.addTask(task);

        Task updatedTask = new Task("Обновленная задача", "Обновленное описание");
        updatedTask.setId(task.getId());

        taskManager.updateTask(updatedTask);

        Task result = taskManager.getTaskById(task.getId());
        assertEquals("Обновленная задача", result.getName());
        assertEquals("Обновленное описание", result.getDescription());
    }

    @Test
    void epicUpdatesCorrectly() {
        Epic epic = new Epic("Test epicUpdatesCorrectly Epic", "Test epicUpdatesCorrectly description");
        taskManager.addEpic(epic);

        Epic updatedEpic = new Epic("Обновленный эпик", "Обновленное описание");
        updatedEpic.setId(epic.getId());

        taskManager.updateEpic(updatedEpic);

        Epic result = taskManager.getEpicById(epic.getId());
        assertEquals("Обновленный эпик", result.getName());
        assertEquals("Обновленное описание", result.getDescription());
    }

    @Test
    void subtaskUpdatesCorrectly() {
        Epic epic = new Epic("Test subtaskUpdatesCorrectly Epic", "Test subtaskUpdatesCorrectly epic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test subtaskUpdatesCorrectly Subtask", "Test subtaskUpdatesCorrectly subtask description", epic.getId());
        taskManager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Обновленный сабтаск", "Обновленное описание", epic.getId());
        updatedSubtask.setId(subtask.getId());
        updatedSubtask.setStatus(Status.DONE);

        taskManager.updateSubtask(updatedSubtask);

        Subtask result = taskManager.getSubtaskById(subtask.getId());
        assertEquals("Обновленный сабтаск", result.getName());
        assertEquals("Обновленное описание", result.getDescription());
        assertEquals(Status.DONE, result.getStatus());
    }

    @Test
    void removeTaskById() {
        Task task = new Task("Test removeTaskById Task", "Test removeTaskById description");
        taskManager.addTask(task);

        taskManager.removeTaskById(task.getId());

        assertTrue(taskManager.getAllTasks().isEmpty(), "Задача должна быть удалена");
    }

    @Test
    void removeEpicByIdAndSubtasks() {
        Epic epic = new Epic("Test removeEpicByIdAndSubtasks Epic", "Test removeEpicByIdAndSubtasks description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        taskManager.addSubtask(subtask1);

        taskManager.removeEpicById(epic.getId());

        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпик должен быть удален");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Сабтаски эпика должны быть удалены");
    }

    @Test
    void removeSubtaskByIdAndUpdatesEpic() {
        Epic epic = new Epic("Test removeSubtaskByIdAndUpdatesEpic Epic", "ETest removeSubtaskByIdAndUpdatesEpic description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.removeSubtaskById(subtask.getId());

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Сабтаск должен быть удален");
        assertTrue(taskManager.getAllSubtasksByEpicId(epic.getId()).isEmpty(), "Эпик должен больше не содержать сабтасков");
    }

    @Test
    void updateEpicStatus() {
        Epic epic = new Epic("Test updateEpicStatus Epic", "Test updateEpicStatus description");
        taskManager.addEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Эпик должен иметь статус NEW");

        Subtask sub1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask sub2 = new Subtask("Subtask 2", "Description", epic.getId());
        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub2);

        taskManager.updateEpic(epic);
        assertEquals(Status.NEW, epic.getStatus(), "Эпик должен быть NEW и все сабтаски NEW");

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        taskManager.updateSubtask(sub1);
        taskManager.updateSubtask(sub2);
        assertEquals(Status.DONE, epic.getStatus(), "Эпик должен быть DONE и все сабтаски DONE");

        sub1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(sub1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик должен быть IN_PROGRESS, если сабтаски с разным статусом");
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

    @Test
    void taskHasIdAfterCreation() {
        Task task = new Task("Test taskHasIdAfterCreation Task", "Test taskHasIdAfterCreation description");
        assertNotNull(task.getId(), "У задачи появляется id сразу после создания.");
    }

    @Test
    void epicCannotContainItselfAsSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test epicCannotContainItselfAsSubtask Epic", "Test epicCannotContainItselfAsSubtask description");
        taskManager.addEpic(epic);

        Subtask invalidSubtask = new Subtask("Неверный Subtask", "Description", epic.getId() + 100);
        taskManager.addSubtask(invalidSubtask);

        assertFalse(epic.getEpicsId().contains(epic.getId()), "Epic не должен содержать себя как сабтаск");

        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Сабтаск не должен быть добавлен, если привязан к несуществующему эпику");
    }
}