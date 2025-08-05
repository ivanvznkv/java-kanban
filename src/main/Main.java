//Сергей, добрый день! Код на ревью)
//Хорошего дня вам!

package main;
import service.TaskManager;
import data.Task;
import data.EpicTask;
import data.SubTask;
import data.Status;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        //Создание и добавление в taskManager
        System.out.println("-----Добавление задач-----");
        Task task1 = new Task(0, "Сходить в магазин", "Купить: яблоки, молоко, бананы", Status.NEW);
        taskManager.addTask(task1);

        Task task2 = new Task(0, "Постирать одежду", "Вещи в корзине", Status.NEW);
        taskManager.addTask(task2);

        EpicTask epicTask1 = new EpicTask(0, "Доделать проектную работу", "Дописать оставшийся код");
        taskManager.addEpicTask(epicTask1);

        SubTask subTask1 = new SubTask(0,"Дописать код main", "Описание в тз", epicTask1.getId());
        taskManager.addSubTask(subTask1);

        EpicTask epicTask2 = new EpicTask(0, "Подготовиться к поездке", ".....");
        taskManager.addEpicTask(epicTask2);

        SubTask subTask2 = new SubTask(0, "Собрать вещи", "По списку", epicTask2.getId());
        taskManager.addSubTask(subTask2);

        SubTask subTask3 = new SubTask(0, "Проверить давление в шинах", ".....", epicTask2.getId());
        taskManager.addSubTask(subTask3);

        //печать задач всех видов
        System.out.println("-----Все задачи-----");
        System.out.println(taskManager.getAllTasks());
        System.out.println("-----Все эпики-----");
        System.out.println(taskManager.getAllEpicTasks());
        System.out.println("-----Все сабтаски-----");
        System.out.println(taskManager.getAllSubTasks());

        //обновление статусов
        System.out.println("-----Обновление статусов-----");
        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        task2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task2);

        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);

        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask2);

        subTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask3);

        System.out.println("-----Обновленные статусы-----");
        System.out.println("Задача c ID: " + task1.getId() + " - статус " + taskManager.getTaskById(task1.getId()).getStatus());
        System.out.println("Задача c ID: " + task2.getId() + " - статус " + taskManager.getTaskById(task2.getId()).getStatus());
        System.out.println("Эпик c ID: " + epicTask1.getId() + " - статус " + taskManager.getEpicTaskById(epicTask1.getId()).getStatus());
        System.out.println("Сабтаск c ID: " + subTask1.getId() + " - статус " + taskManager.getSubTaskById(subTask1.getId()).getStatus());
        System.out.println("Эпик c ID: " + epicTask2.getId() + " - статус " + taskManager.getEpicTaskById(epicTask2.getId()).getStatus());
        System.out.println("Сабтаск c ID: " + subTask2.getId() + " - статус " + taskManager.getSubTaskById(subTask2.getId()).getStatus());
        System.out.println("Сабтаск c ID: " + subTask3.getId() + " - статус " + taskManager.getSubTaskById(subTask3.getId()).getStatus());

        //удаление
        System.out.println("-----Удаление-----");
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicTaskById(epicTask1.getId());

        System.out.println("-----Все задачи-----");
        System.out.println(taskManager.getAllTasks());
        System.out.println("-----Все эпики-----");
        System.out.println(taskManager.getAllEpicTasks());
        System.out.println("-----Все сабтаски-----");
        System.out.println(taskManager.getAllSubTasks());
    }
}