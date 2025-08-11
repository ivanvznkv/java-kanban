package service;

import data.Task;
import data.Epic;
import data.Subtask;
import data.Status;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int nextTaskId = 1;

    // методы add
    public void addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(task.getId(), task);
        System.out.println("Задача с ID: " + task.getId() + " добавлена");
    }

    public void addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(epic.getId(), epic);
        System.out.println("Эпик с ID: " + epic.getId() + " добавлен");
    }

    public void addSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtask.getId(), subtask);
            epic.addEpicIds(subtask.getId());
            updateEpicStatus(epic.getId());
            System.out.println("Сабтаск с ID: " + subtask.getId() + " добавлен");
        } else {
            System.out.println("Сабтаск не может быть создан! Эпик с ID " + subtask.getEpicId() + " не найден");
        }
    }

    // геттеры получения списка задач одного типа
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getAllSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer id : epic.getEpicsId()) {
                Subtask subtask = subtasks.get(id);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    // геттеры получения задачи по id
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // методы удаления всех задач одного типа
    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subtasksIds = epic.getEpicsId();
            for (Integer id : subtasksIds) {
                subtasks.remove(id);
            }
        }
        epics.clear();
        System.out.println("Все эпики и связанные сабтаски удалены");
    }

    public void removeAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicsId().clear();
            updateEpicStatus(epic.getId());
        }
        System.out.println("Все сабтаски удалены.");
    }

    // методы обновления задач
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            tasks.put(task.getId(), task);
            System.out.println("Задача с ID:" + task.getId() + " обновлена");
        } else {
            System.out.println("Задача с ID:" + task.getId() + " не найдена");
        }
    }

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

    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask != null) {
            oldSubtask.setName(subtask.getName());
            oldSubtask.setDescription(subtask.getDescription());
            oldSubtask.setStatus(subtask.getStatus());
            updateEpicStatus(subtask.getEpicId());
            System.out.println("Сабтаск с ID:" + subtask.getId() + " обновлен");
        } else {
            System.out.println("Сабтаск с ID " + subtask.getId() + " не найден");
        }
    }

    // методы удаления по id
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача с ID " + id + " удалена");
        } else {
            System.out.println("Задача с ID " + id + " не найдена");
        }
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtasksId : epic.getEpicsId()) {
                subtasks.remove(subtasksId);
            }
            epics.remove(epic.getId());
            System.out.println("Эпик с ID " + id + " и его подзадачи удалены");
        } else {
            System.out.println("Эпик с ID " + id + " не найден");
        }
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtasks.remove(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
            System.out.println("Сабтаск с ID " + id + " удален");
        } else {
            System.out.println("Сабтаск с ID " + id + " не найден");
        }
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
        boolean allNew = true;
        boolean allDone = true;

        for (Integer id : subtasksIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
        }
        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}