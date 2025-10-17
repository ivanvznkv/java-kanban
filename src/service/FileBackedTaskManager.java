package service;

import data.Epic;
import data.Subtask;
import data.Task;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
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
            bw.write("id,type,name,status,description,duration,startTime,epic");
            bw.newLine();

            for (Task task : getAllTasks()) {
                bw.write(TaskConverter.toString(task));
                bw.newLine();
            }

            for (Epic epic : getAllEpics()) {
                bw.write(TaskConverter.toString(epic));
                bw.newLine();
            }

            for (Subtask subtask : getAllSubtasks()) {
                bw.write(TaskConverter.toString(subtask));
                bw.newLine();
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении файла: "
                    + saveFile.getName(), exception);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                Task task = TaskConverter.fromString(line);

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                switch (task.getType()) {
                    case TASK -> manager.tasks.put(task.getId(), task);
                    case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        Epic epic = manager.epics.get(((Subtask) task).getEpicId());
                        if (epic != null) {
                            epic.addEpicIds(task.getId());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла: " + file.getName(), e);
        }

        manager.nextTaskId = maxId + 1;

        return manager;
    }
}
