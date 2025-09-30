package service;

import data.*;

public class TaskConverter {
    public static String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");

        if (task.getType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getEpicId());
        } else {
            sb.append("");
        }

        return sb.toString();
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        if (fields.length < 5) {
            throw new IllegalArgumentException("Неверный формат: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields.length > 2 ? fields[2] : "";
        Status status = Status.valueOf(fields[3]);
        String description = fields.length > 4 ? fields[4] : "";

        Task task;
        switch (type) {
            case TASK -> {
                task = new Task(name, description, TaskType.TASK);
            }
            case EPIC -> {
                task = new Epic(name, description);
            }
            case SUBTASK -> {
                int epicId = fields.length > 5 && !fields[5].isEmpty() ? Integer.parseInt(fields[5]) : 0;
                task = new Subtask(name, description, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        task.setStatus(status);
        return task;
    }
}
