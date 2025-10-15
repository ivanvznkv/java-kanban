import service.*;
import data.*;

import java.time.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        Subtask invalid = new Subtask("Invalid subtask", "Invalid subtask description", epic.getId());
        invalid.setId(epic.getId());

        manager.addSubtask(invalid);

        assertTrue(manager.getAllSubtasks().isEmpty(),
                "Сабтаск не должен быть добавлен, если он ссылается сам на себя");
    }

    @Test
    void epicCannotBeAddedAsItsOwnSubtask() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        Subtask invalid = new Subtask("Invalid subtask", "Invalid subtask description", epic.getId() + 100);
        manager.addSubtask(invalid);

        assertFalse(epic.getEpicsId().contains(epic.getId()), "Эпик не должен содержать себя как сабтаск");
    }

    @Test
    void subtaskNotAddedIfEpicNotFound() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Subtask subtask = new Subtask("Subtask1", "Subtask1 description", 999);
        manager.addSubtask(subtask);

        assertTrue(manager.getAllSubtasks().isEmpty(), "Сабтаск не должен добавляться, если эпик не найден");
    }

    @Test
    void shouldSetEpicStatusNewWhenAllSubtasksNew() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.NEW);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus(), "Если все подзадачи NEW, то эпик должен быть NEW");
    }

    @Test
    void shouldSetEpicStatusDoneWhenAllSubtasksDone() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus(), "Если все подзадачи DONE, то эпик должен быть DONE");
    }

    @Test
    void shouldSetEpicStatusInProgressWhenMixedNewAndDone() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.DONE);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus(), "Если подзадачи NEW и DONE, эпик должен быть IN_PROGRESS");
    }

    @Test
    void shouldSetEpicStatusInProgressWhenAnySubtaskInProgress() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        sub1.setStatus(Status.IN_PROGRESS);
        sub2.setStatus(Status.NEW);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus(), "Если хотя бы одна подзадача IN_PROGRESS, эпик должен быть IN_PROGRESS");
    }

    @Test
    void subtaskShouldHaveValidEpicReference() {
        Epic epic = new Epic("Epic1", "Epic description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        manager.addSubtask(subtask);

        Subtask savedSubtask = manager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask, "Сабтаск должен сохраняться в менеджере");
        assertEquals(epic.getId(), savedSubtask.getEpicId(), "Сабтаск должен ссылаться на корректный ID эпика");
    }

    @Test
    void updateEpicTimeShouldCalculateDurationAndStartTime() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Subtask1 description", epic.getId());
        sub1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        sub1.setDuration(Duration.ofMinutes(60));

        Subtask sub2 = new Subtask("Subtask2", "Subtask2 description", epic.getId());
        sub2.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        sub2.setDuration(Duration.ofMinutes(120));

        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        Subtask addedSub1 = manager.getAllSubtasks().stream()
                .filter(s -> s.getName().equals("Subtask1")).findFirst().orElseThrow();
        Subtask addedSub2 = manager.getAllSubtasks().stream()
                .filter(s -> s.getName().equals("Subtask2")).findFirst().orElseThrow();

        Epic updatedEpic = manager.getEpicById(epic.getId());

        assertEquals(Duration.ofMinutes(180), updatedEpic.getDuration(), "Неверная суммарная продолжительность эпика");
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), updatedEpic.getStartTime(), "Неверное время начала эпика");
    }

    @Test
    void overlappingTaskShouldNotBeAdded() {
        int initialTaskCount = manager.getAllTasks().size();

        Task task1 = new Task("Task1", "Первая задача");
        task1.setStartTime(LocalDateTime.of(2025, 10, 11, 9, 0));
        task1.setDuration(Duration.ofHours(2));
        manager.addTask(task1);

        Task overlappingTask = new Task("Overlap Task", "Пересекается с Task1");
        overlappingTask.setStartTime(LocalDateTime.of(2025, 10, 11, 10, 0));
        overlappingTask.setDuration(Duration.ofHours(1));
        manager.addTask(overlappingTask);

        assertEquals(initialTaskCount + 1, manager.getAllTasks().size(), "Пересекающаяся задача не должна добавляться");
    }
}