package service;

import data.*;

import java.util.*;


public interface TaskManager {
    // методы add
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    // геттеры получения списка задач одного типа
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getAllSubtasksByEpicId(int epicId);

    // геттеры получения задачи по id
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    // методы удаления всех задач одного типа
    void removeAllTasks();

    void removeAllEpic();

    void removeAllSubtask();

    // методы обновления задач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // методы удаления по id
    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    ArrayList<Task> getHistory();

    List<Task> getPrioritizedTasks();
}