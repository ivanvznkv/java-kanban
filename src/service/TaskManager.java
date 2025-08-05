package service;

import data.Task;
import data.EpicTask;
import data.SubTask;
import data.Status;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int nextTaskId = 1;

    //генерация ID
    private int generateId() {
        return nextTaskId++;
    }

    // методы add
    public void addTask(Task task) {
        if (task.getId() == 0) {
            int id = generateId();
            task.setId(id);
        }
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
        System.out.println("Задача с ID: " + task.getId() + " добавлена");
    }

    public void addEpicTask(EpicTask epicTask) {
        if (epicTask.getId() == 0) {
            int id = generateId();
            epicTask.setId(id);
        }
        epicTask.setStatus(Status.NEW);
        epicTasks.put(epicTask.getId(), epicTask);
        System.out.println("Эпик с ID: " + epicTask.getId() + " добавлен");
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getId() == 0) {
            int id = generateId();
            subTask.setId(id);
        }
        EpicTask epicTask = epicTasks.get(subTask.getEpicId());
        if (epicTask != null) {
            subTasks.put(subTask.getId(), subTask);
            epicTask.addEpicTasksIds(subTask.getId());
            updateEpicStatus(epicTask.getId());
            System.out.println("Сабтаск с ID: " + subTask.getId() + " добавлен");
        } else {
            System.out.println("Сабтаск не может быть создан! Эпик с ID " + subTask.getEpicId() + " не найден");
        }
    }

    // геттеры получения списка задач одного типа
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<SubTask> getAllSubTasksByEpicId(int epicId) {
        ArrayList<SubTask> result = new ArrayList<>();
        EpicTask epicTask = epicTasks.get(epicId);
        if (epicTask != null) {
            for (Integer id : epicTask.getEpicTasksId()) {
                SubTask subTask = subTasks.get(id);
                if (subTask != null) {
                    result.add(subTask);
                }
            }
        }
        return result;
    }

    // геттеры получения задачи по id
    public Task getTaskById(int id) {
       Task task = tasks.get(id);
       if (task != null) {
           return task;
       } else {
           System.out.println("Задача с ID " + id + " не найдена");
           return null;
       }
    }

    public EpicTask getEpicTaskById(int id) {
        EpicTask epicTask = epicTasks.get(id);
        if (epicTask != null) {
            return epicTask;
        } else {
            System.out.println("Эпик с ID " + id + " не найден");
            return null;
        }
    }

    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            return subTask;
        } else {
            System.out.println("Сабтаск с ID " + id + " не найден");
            return null;
        }
    }

    // методы удаления всех задач одного типа
    public void removeAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст — удаление невозможно");
            return;
        }
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    public void removeAllEpicTask() {
        if (epicTasks.isEmpty()) {
            System.out.println("Список эпиков пуст — удаление невозможно");
            return;
        }
        for (EpicTask epicTask : epicTasks.values()) {
            ArrayList<Integer> subTasksIds = epicTask.getEpicTasksId();
            for (Integer id : subTasksIds) {
                subTasks.remove(id);
            }
        }
        epicTasks.clear();
        System.out.println("Все эпики и связанные сабтаски удалены");
    }

    public void removeAllSubTask() {
        if (subTasks.isEmpty()) {
            System.out.println("Список сабтасков пуст — удаление невозможно");
            return;
        }
        subTasks.clear();
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.getEpicTasksId().clear();
            updateEpicStatus(epicTask.getId());
        }
        System.out.println("Все сабтаски удалены.");
    }

    //методы обновления задач
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null) {
            tasks.put(task.getId(), task);
            System.out.println("Задача с ID:" + task.getId() + " обновлена");
        } else {
            System.out.println("Задача с ID:" + task.getId() + " не найдена");
        }
    }

    public void updateEpicTask(EpicTask epicTask) {
        EpicTask oldEpicTask = epicTasks.get(epicTask.getId());
        if (oldEpicTask != null) {
            ArrayList<Integer> subTasksIds = oldEpicTask.getEpicTasksId();
            epicTasks.put(epicTask.getId(), epicTask);

            epicTask.getEpicTasksId().clear();
            for (Integer id : subTasksIds) {
                epicTask.getEpicTasksId().add(id);
            }
            updateEpicStatus(epicTask.getId());
            System.out.println("Эпик с ID:" + epicTask.getId() + " обновлен");
        } else {
            System.out.println("Эпик с ID:" + epicTask.getId() + " не найден");
        }
    }

    public void updateEpicStatus(int epicId) {
        EpicTask epicTask = epicTasks.get(epicId);
        if (epicTask == null) {
            System.out.println("Эпик с ID " + epicId + " не найден.");
            return;
        }

        ArrayList<Integer> subTasksIds = epicTask.getEpicTasksId();
        if (subTasksIds.isEmpty()) {
            epicTask.setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;

        for (Integer id : subTasksIds) {
            SubTask subTask = subTasks.get(id);
            if (subTask != null) {
                if (subTask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subTask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
        }
        if (allNew) {
            epicTask.setStatus(Status.NEW);
        } else if (allDone) {
            epicTask.setStatus(Status.DONE);
        } else {
            epicTask.setStatus(Status.IN_PROGRESS);
        }
    }

    public void updateSubTask(SubTask subTask) {
        SubTask oldSubTask = subTasks.get(subTask.getId());
        if (oldSubTask != null) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId());
            System.out.println("Сабтаск с ID:" + subTask.getId() + " обновлен");
        } else {
            System.out.println("Сабтаск с ID " + subTask.getId() + " не найден");
        }
    }

    //методы удаления по id
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача с ID " + id + " удалена");
        } else {
            System.out.println("Задача с ID " + id + " не найдена");
        }
    }

    public void removeEpicTaskById(int id) {
        EpicTask epicTask = epicTasks.get(id);
        if (epicTask != null) {
            for (Integer subTasksId : epicTask.getEpicTasksId()) {
                subTasks.remove(subTasksId);
            }
            epicTasks.remove(epicTask.getId());
            System.out.println("Эпик с ID " + id + " и его подзадачи удалены");
        } else {
            System.out.println("Эпик с ID " + id + " не найден");
        }
    }

    public void removeSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            subTasks.remove(subTask.getId());
            EpicTask epicTask = epicTasks.get(subTask.getEpicId());
            if (epicTask != null) {
                epicTask.removeSubtaskId(id);
                updateEpicStatus(epicTask.getId());
            }
            System.out.println("Сабтаск с ID " + id + " удален");
        } else {
            System.out.println("Сабтаск с ID " + id + " не найден");
        }
    }
}