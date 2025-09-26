package service;

import data.Epic;
import data.Subtask;
import data.Task;
import data.TaskType;
import data.Status;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(saveFile.toPath(), StandardCharsets.UTF_8)) {
            bw.write("id,type,name,status,description,epic");
            bw.newLine();

            for (Task task : getAllTasks()) {
                bw.write(toString(task));
                bw.newLine();
            }

            for (Epic epic : getAllEpics()) {
                bw.write(toString(epic));
                bw.newLine();
            }

            for (Subtask subtask : getAllSubtasks()) {
                bw.write(toString(subtask));
                bw.newLine();
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении файла: "
                    + saveFile.getName(), exception);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                    Epic epic = manager.epics.get(((Subtask) task).getEpicId());
                    if (epic != null) {
                        epic.addEpicIds(task.getId());
                    }
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла: " + file.getName(), e);
        }

        int maxId = 0;
        for (Task task : manager.getAllTasks()) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        for (Epic epic : manager.getAllEpics()) {
            if (epic.getId() > maxId) {
                maxId = epic.getId();
            }
        }

        for (Subtask subtask : manager.getAllSubtasks()) {
            if (subtask.getId() > maxId) {
                maxId = subtask.getId();
            }
        }
        manager.nextTaskId = maxId + 1;

        return manager;
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        if (task instanceof Epic) {
            sb.append(TaskType.EPIC).append(",");
        } else if (task instanceof Subtask) {
            sb.append(TaskType.SUBTASK).append(",");
        } else {
            sb.append(TaskType.TASK).append(",");
        }

        sb.append(task.getName()).append(",");
        sb.append(task.getStatus().toString()).append(",");
        sb.append(task.getDescription()).append(",");

        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        } else {
            sb.append("");
        }
        return sb.toString();
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK -> {
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            }
            default ->
                    throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}