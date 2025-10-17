import service.*;
import data.*;

import java.util.*;
import java.time.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1, subtask2;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();

        task = new Task("Task1", "Task1 description");
        task.setStartTime(LocalDateTime.of(2025, 10, 11, 9,0));
        task.setDuration(Duration.ofHours(1));
        manager.addTask(task);

        epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        subtask1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        subtask1.setStartTime(LocalDateTime.of(2025, 10, 11, 11,0));
        subtask1.setDuration(Duration.ofHours(2));
        manager.addSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        subtask2.setStartTime(LocalDateTime.of(2025, 10, 11, 14,0));
        subtask2.setDuration(Duration.ofHours(3));
        manager.addSubtask(subtask2);
    }

    @Test
    void addAndRetrieveTasksOfDifferentTypes() {
        Task task1 = new Task("Task1", "Task1 description");
        manager.addTask(task1);

        Epic epic1 = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        manager.addSubtask(subtask1);

        assertEquals(task1, manager.getTaskById(task1.getId()), "Task1 не найдена по id");
        assertEquals(epic1, manager.getEpicById(epic1.getId()), "Epic1 не найден по id");
        assertEquals(subtask1, manager.getSubtaskById(subtask1.getId()), "Subtask1 не найден по id");
    }

    @Test
    void addNewTask() {
        Task task1 = new Task("Task1", "Task1 description");
        assertEquals(Status.NEW, task1.getStatus());

        manager.addTask(task1);
        Task savedTask = manager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Task1 не найдена после добавления");
        assertEquals(task1, savedTask, "Task1 сохранена некорректно");

        List<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks, "Список всех задач пуст");
        assertTrue(tasks.contains(task1), "Список задач должен содержать Task1");
    }

    @Test
    void getAllEpicsReturnsAllEpics() {
        manager.removeAllEpic();

        Epic epic1 = new Epic("Epic1", "Epic1 description");
        Epic epic2 = new Epic("Epic2", "Epic2 description");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epics, "Список эпиков пуст");
        assertEquals(2, epics.size(), "Должно быть 2 эпика");
        assertTrue(epics.contains(epic1), "Список эпиков должен содержать Epic1");
        assertTrue(epics.contains(epic2), "Список эпиков должен содержать Epic2");
    }

    @Test
    void getAllSubtasksByEpicIdReturnsOnlyItsSubtasks() {
        Epic epic1 = new Epic("Epic1", "Epic1 description");
        Epic epic2 = new Epic("Epic2", "Epic2 description");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 description", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 description", epic1.getId());
        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 description", epic2.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        List<Subtask> subtasks = manager.getAllSubtasksByEpicId(epic1.getId());
        assertNotNull(subtasks, "Список сабтасков не должен быть null");
        assertEquals(2, subtasks.size(), "Epic1 должен содержать 2 сабтаска");
        assertTrue(subtasks.contains(subtask1), "Список сабтасков Epic1 должен содержать Subtask1");
        assertTrue(subtasks.contains(subtask2), "Список сабтасков Epic1 должен содержать Subtask2");
        assertFalse(subtasks.contains(subtask3), "Список не должен содержать сабтаск другого эпика");
    }

    @Test
    void getByIdReturnsCorrectTasks() {
        assertEquals(task, manager.getTaskById(task.getId()), "Task1 не найдена по id");
        assertEquals(epic, manager.getEpicById(epic.getId()), "Epic1 не найден по id");
        assertEquals(subtask1, manager.getSubtaskById(subtask1.getId()), "Subtask1 не найден по id");
        assertEquals(subtask2, manager.getSubtaskById(subtask2.getId()), "Subtask2 не найден по id");
    }

    @Test
    void getByIdReturnsNullForNonExistentId() {
        assertNull(manager.getTaskById(100), "Task с несуществующим id должен возвращать null");
        assertNull(manager.getEpicById(100), "Epic с несуществующим id должен возвращать null");
        assertNull(manager.getSubtaskById(100), "Subtask с несуществующим id должен возвращать null");
    }

    @Test
    void removeAllTasksRemovesOnlyTasks() {
        manager.removeAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
        assertFalse(manager.getAllEpics().isEmpty(), "Эпики не должны быть удалены");
        assertFalse(manager.getAllSubtasks().isEmpty(), "Сабтаски не должны быть удалены");
    }

    @Test
    void removeAllEpicRemovesEpicsAndSubtasks() {
        manager.removeAllEpic();
        assertTrue(manager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Все сабтаски эпиков должны быть удалены");
        assertFalse(manager.getAllTasks().isEmpty(), "Задачи не должны быть удалены");
    }

    @Test
    void removeAllSubtasksClearsEpicSubtaskLists() {
        manager.removeAllSubtask();
        assertTrue(manager.getAllSubtasks().isEmpty(), "Все сабтаски должны быть удалены");
        assertTrue(manager.getAllSubtasksByEpicId(epic.getId()).isEmpty(), "Списки сабтасков Epic должны быть очищены");
        assertFalse(manager.getAllTasks().isEmpty(), "Задачи не должны быть удалены");
        assertFalse(manager.getAllEpics().isEmpty(), "Эпики не должны быть удалены");
    }

    @Test
    void updateTaskWorksCorrectly() {
        Task updated = new Task("UpdatedTask", "UpdatedTask description");
        updated.setId(task.getId());
        manager.updateTask(updated);

        Task result = manager.getTaskById(task.getId());
        assertEquals("UpdatedTask", result.getName(), "Имя задачи не обновилось");
        assertEquals("UpdatedTask description", result.getDescription(), "Описание задачи не обновилось");
    }

    @Test
    void updateEpicWorksCorrectly() {
        Epic updated = new Epic("UpdatedEpic", "UpdatedEpic description");
        updated.setId(epic.getId());
        manager.updateEpic(updated);

        Epic result = manager.getEpicById(epic.getId());
        assertEquals("UpdatedEpic", result.getName(), "Имя эпика не обновилось");
        assertEquals("UpdatedEpic description", result.getDescription(), "Описание эпика не обновилось");
    }

    @Test
    void updateSubtaskWorksCorrectly() {
        Subtask updated = new Subtask("UpdatedSubtask", "UpdatedSubtask description", epic.getId());
        updated.setId(subtask1.getId());
        updated.setStatus(Status.DONE);
        manager.updateSubtask(updated);

        Subtask result = manager.getSubtaskById(subtask1.getId());
        assertEquals("UpdatedSubtask", result.getName(), "Имя сабтаска не обновилось");
        assertEquals("UpdatedSubtask description", result.getDescription(), "Описание сабтаска не обновилось");
        assertEquals(Status.DONE, result.getStatus(), "Статус сабтаска не обновился");
    }

    @Test
    void removeTaskByIdWorks() {
        manager.removeTaskById(task.getId());
        assertNull(manager.getTaskById(task.getId()), "Task1 должен быть удален по id");
    }

    @Test
    void removeEpicByIdRemovesItsSubtasks() {
        manager.removeEpicById(epic.getId());
        assertNull(manager.getEpicById(epic.getId()), "Epic1 должен быть удален по id");
        assertTrue(manager.getAllSubtasksByEpicId(epic.getId()).isEmpty(), "Сабтаски Epic1 должны быть удалены");
    }

    @Test
    void removeSubtaskByIdUpdatesEpic() {
        manager.removeSubtaskById(subtask1.getId());
        assertNull(manager.getSubtaskById(subtask1.getId()), "Subtask1 должен быть удален по id");
        assertFalse(manager.getAllSubtasksByEpicId(epic.getId()).contains(subtask1), "Epic1 не должен содержать удаленный Subtask1");
    }

    @Test
    void epicStatusUpdatesCorrectly() {
        Epic newEpic = new Epic("EpicStatus", "EpicStatus description");
        manager.addEpic(newEpic);

        Subtask s1 = new Subtask("Subtask1", "Subtask1 description", newEpic.getId());
        Subtask s2 = new Subtask("Subtask2", "Subtask2 description", newEpic.getId());
        manager.addSubtask(s1);
        manager.addSubtask(s2);

        manager.updateEpic(newEpic);
        assertEquals(Status.NEW, newEpic.getStatus(), "Все новые сабтаски => статус NEW");

        s1.setStatus(Status.DONE);
        s2.setStatus(Status.DONE);
        manager.updateSubtask(s1);
        manager.updateSubtask(s2);
        assertEquals(Status.DONE, newEpic.getStatus(), "Все сабтаски DONE => эпик DONE");

        s1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(s1);
        assertEquals(Status.IN_PROGRESS, newEpic.getStatus(), "Сабтаски с разными статусами => эпик IN_PROGRESS");
    }

    @Test
    void noIdConflictBetweenGeneratedAndExplicitIds() {
        Task explicit = new Task("ExplicitTask", "ExplicitTask description");
        explicit.setId(100);
        manager.addTask(explicit);

        Task auto = new Task("AutoTask", "AutoTask description");
        manager.addTask(auto);

        assertNotEquals(explicit.getId(), auto.getId(), "ID автогенерации не должен конфликтовать с явным ID");
    }

    @Test
    void taskRemainsImmutableAfterAdd() {
        Task immutable = new Task("ImmutableTask", "ImmutableTask description");
        immutable.setStatus(Status.IN_PROGRESS);

        String name = immutable.getName();
        String desc = immutable.getDescription();
        Status status = immutable.getStatus();

        manager.addTask(immutable);

        assertEquals(name, immutable.getName(), "Имя задачи изменилось после добавления");
        assertEquals(desc, immutable.getDescription(), "Описание задачи изменилось после добавления");
        assertEquals(status, immutable.getStatus(), "Статус задачи изменился после добавления");
    }

    @Test
    void taskHasIdAfterCreation() {
        Task task = new Task("Task1", "Task1 description");
        manager.addTask(task);

        assertNotNull(task.getId(), "У задачи должен появляться id после создания");
        assertTrue(task.getId() > 0, "Id задачи должен быть положительным числом");

        Task savedTask = manager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена по id после добавления");
        assertEquals(task, savedTask, "Добавленная и полученная задачи не совпадают");
    }

    @Test
    void getPrioritizedTasksShouldReturnTasksSortedByStartTime() {
        List<Task> prioritized = new ArrayList<>(manager.getPrioritizedTasks());

        assertNotNull(prioritized, "Список приоритетных задач не должен быть null");
        assertEquals(3, prioritized.size(), "Должно быть 3 задачи (1 Task + 2 Subtask)");

        assertEquals(task, prioritized.get(0), "Первая должна быть основная задача (Task1)");
        assertEquals(subtask1, prioritized.get(1), "Второй должна быть Subtask1");
        assertEquals(subtask2, prioritized.get(2), "Третьей должна быть Subtask2");

        assertFalse(prioritized.contains(epic), "Epic не должен входить в приоритетные задачи, так как у него нет времени");
    }
}