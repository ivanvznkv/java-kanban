package data;

import java.time.*;
import java.util.*;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description, TaskType type) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(Task other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.status = other.status;
        this.duration = other.duration;
        this.startTime = other.startTime;
    }

    public Task(String name, String description) {
        this(name, description, TaskType.TASK);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        if (startTime == null || endTime == null) return;
        duration = Duration.between(startTime, endTime);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{\n" +
                " ID = " + id + ",\n" +
                " name = '" + name + "',\n" +
                " description = '" + description + "',\n" +
                " status = " + status + "\n" +
                " startTime = " + getStartTime() + ",\n" +
                " duration = " + getDuration() + ",\n" +
                " endTime = " + getEndTime() + "\n" +
                '}';
    }
}
