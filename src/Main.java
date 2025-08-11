//Сергей, правки внес, код на ревью)
//Хорошего дня вам!

import service.TaskManager;
import data.Task;
import data.Epic;
import data.Subtask;
import data.Status;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        // Создание и добавление в taskManager
        System.out.println("-----Добавление задач-----");
        Task task1 = new Task("Сходить в магазин", "Купить: яблоки, молоко, бананы");
        taskManager.addTask(task1);

        Task task2 = new Task("Постирать одежду", "Вещи в корзине");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Доделать проектную работу", "Дописать оставшийся код");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Дописать код main", "Описание в тз", epic1.getId());
        taskManager.addSubtask(subtask1);

        Epic epic2 = new Epic("Подготовиться к поездке", ".....");
        taskManager.addEpic(epic2);

        Subtask subtask2 = new Subtask("Собрать вещи", "По списку", epic2.getId());
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Проверить давление в шинах", ".....", epic2.getId());
        taskManager.addSubtask(subtask3);

        // печать задач всех видов
        System.out.println("-----Все задачи-----");
        System.out.println(taskManager.getAllTasks());
        System.out.println("-----Все эпики-----");
        System.out.println(taskManager.getAllEpics());
        System.out.println("-----Все сабтаски-----");
        System.out.println(taskManager.getAllSubtasks());

        // обновление статусов
        System.out.println("-----Обновление статусов-----");
        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        task2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task2);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        System.out.println("-----Обновленные статусы-----");
        System.out.println("Задача c ID: " + task1.getId() + " - статус " + taskManager.getTaskById(task1.getId()).getStatus());
        System.out.println("Задача c ID: " + task2.getId() + " - статус " + taskManager.getTaskById(task2.getId()).getStatus());
        System.out.println("Эпик c ID: " + epic1.getId() + " - статус " + taskManager.getEpicById(epic1.getId()).getStatus());
        System.out.println("Сабтаск c ID: " + subtask1.getId() + " - статус " + taskManager.getSubtaskById(subtask1.getId()).getStatus());
        System.out.println("Эпик c ID: " + epic2.getId() + " - статус " + taskManager.getEpicById(epic2.getId()).getStatus());
        System.out.println("Сабтаск c ID: " + subtask2.getId() + " - статус " + taskManager.getSubtaskById(subtask2.getId()).getStatus());
        System.out.println("Сабтаск c ID: " + subtask3.getId() + " - статус " + taskManager.getSubtaskById(subtask3.getId()).getStatus());

        // удаление
        System.out.println("-----Удаление-----");
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());

        System.out.println("-----Все задачи-----");
        System.out.println(taskManager.getAllTasks());
        System.out.println("-----Все эпики-----");
        System.out.println(taskManager.getAllEpics());
        System.out.println("-----Все сабтаски-----");
        System.out.println(taskManager.getAllSubtasks());
    }
}