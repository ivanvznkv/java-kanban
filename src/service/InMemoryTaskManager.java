package service;

import data.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int nextTaskId = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparing(Task::getId)
    );

    // методы add
    @Override
    public void addTask(Task task) {
        if (hasIntersection(task)) {
            System.out.println("Ошибка: задача пересекается по времени с другой задачей!");
            return;
        }

        int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        System.out.println("Задача с ID: " + task.getId() + " добавлена");
    }

    @Override
    public void addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(epic.getId(), epic);
        System.out.println("Эпик с ID: " + epic.getId() + " добавлен");
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(generateId());
        }

        if (subtask.getId() == subtask.getEpicId()) {
            System.out.println("Сабтаск не может быть своим же эпиком!");
            return;
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Сабтаск не может быть создан! Эпик с ID " + subtask.getEpicId() + " не найден");
            return;
        }

        if (tasks.containsKey(subtask.getId()) || subtasks.containsKey(subtask.getId()) || epics.containsKey(subtask.getId())) {
            System.out.println("ID " + subtask.getId() + " уже используется другой задачей/эпиком/сабтаском");
            return;
        }

        if (hasIntersection(subtask)) {
            System.out.println("Ошибка: сабтаск пересекается по времени с другой задачей!");
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);
        epic.addEpicIds(subtask.getId());
        updateEpicStatus(epic.getId());
        updateEpicTime(epic);
        System.out.println("Сабтаск с ID: " + subtask.getId() + " добавлен");
    }

    // геттеры получения списка задач одного типа
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksByEpicId(int epicId) {
        return epics.getOrDefault(epicId, new Epic("", "")).getEpicsId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // геттеры получения задачи по id
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtasks.get(id);
    }

    // методы удаления всех задач одного типа
    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
        }
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            for (Integer id : epic.getEpicsId()) {
                Subtask subtask = subtasks.remove(id);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
            }
        }
        epics.clear();
        System.out.println("Все эпики и связанные сабтаски удалены");
    }

    @Override
    public void removeAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicsId().clear();
            updateEpicStatus(epic.getId());
        }
        System.out.println("Все сабтаски удалены.");
    }

    // методы обновления задач
    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask == null) {
            System.out.println("Ошибка: Задача с ID:" + task.getId() + " не найдена");
            return;
        }

        if (hasIntersection(task)) {
            System.out.println("Ошибка: Обновленная версия задачи пересекается по времени с другой задачей!");
        }

        removeFromPrioritizedTasks(oldTask);
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        System.out.println("Задача с ID:" + task.getId() + " обновлена");
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic != null) {
            oldEpic.setName(epic.getName());
            oldEpic.setDescription(epic.getDescription());

            updateEpicStatus(epic.getId());
            System.out.println("Эпик с ID:" + epic.getId() + " обновлен");
        } else {
            System.out.println("Эпик с ID:" + epic.getId() + " не найден");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask == null) {
            System.out.println("Ошибка: Сабтаск с ID " + subtask.getId() + " не найден");
            return;
        }

        if (hasIntersection(subtask)) {
            System.out.println("Ошибка: Обновленная версия сабтаска пересекается по времени с другой задачей!");
            return;
        }

        prioritizedTasks.remove(oldSubtask);

        oldSubtask.setName(subtask.getName());
        oldSubtask.setDescription(subtask.getDescription());
        oldSubtask.setStatus(subtask.getStatus());
        oldSubtask.setStartTime(subtask.getStartTime());
        oldSubtask.setDuration(subtask.getDuration());

        prioritizedTasks.add(oldSubtask);

        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(epics.get(subtask.getEpicId()));
        System.out.println("Сабтаск с ID:" + subtask.getId() + " обновлен");
    }

    // методы удаления по id
    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
            prioritizedTasks.remove(task);
            System.out.println("Задача с ID " + id + " удалена");
        } else {
            System.out.println("Задача с ID " + id + " не найдена");
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtasksId : epic.getEpicsId()) {
                Subtask subtask = subtasks.remove(subtasksId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
            }
            epics.remove(epic.getId());
            System.out.println("Эпик с ID " + id + " и его подзадачи удалены");
        } else {
            System.out.println("Эпик с ID " + id + " не найден");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtasks.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTime(epic);
            }
            System.out.println("Сабтаск с ID " + id + " удален");
        } else {
            System.out.println("Сабтаск с ID " + id + " не найден");
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    private boolean hasIntersection(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .filter(existingTask -> existingTask.getStartTime() != null && existingTask.getEndTime() != null)
                .anyMatch(existingTask ->
                        task.getStartTime().isBefore(existingTask.getEndTime()) &&
                                task.getEndTime().isAfter(existingTask.getStartTime())
                );
    }

    // генерация ID
    private int generateId() {
        return nextTaskId++;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Эпик с ID " + epicId + " не найден.");
            return;
        }

        ArrayList<Integer> subtasksIds = epic.getEpicsId();
        if (subtasksIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allNew = subtasksIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == Status.NEW);

        boolean allDone = subtasksIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasksList = epic.getEpicsId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (subtasksList.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration totalDuration = subtasksList.stream()
                .filter(s -> s.getDuration() != null)
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime minStart = subtasksList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxEnd = subtasksList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setDuration(totalDuration);
        epic.setStartTime(minStart);
        epic.setEndTime(maxEnd);
    }
}
