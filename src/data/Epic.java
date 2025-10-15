package data;

import java.util.*;
import java.time.*;

public class Epic extends Task {
    private ArrayList<Integer> epicId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getEpicsId() {
        return new ArrayList<>(epicId);
    }

    public void addEpicIds(int id) {
        if (!epicId.contains(id)) {
            epicId.add(id);
        }
    }

    public void removeSubtaskId(int id) {
        Iterator<Integer> iterator = epicId.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == id) {
                iterator.remove();
            }
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{\n" +
                " ID = " + getId() + ",\n" +
                " name = '" + getName() + "',\n" +
                " description = '" + getDescription() + "',\n" +
                " status = '" + getStatus() + "',\n" +
                " startTime = " + getStartTime() + ",\n" +
                " duration = " + getDuration() + ",\n" +
                " endTime = " + getEndTime() + "\n" +
                '}';
    }
}
