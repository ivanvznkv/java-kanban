package data;

import java.util.ArrayList;

public class EpicTask extends Task {
    private ArrayList<Integer> epicTasksId;

    public EpicTask(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.epicTasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getEpicTasksId() {
        return epicTasksId;
    }

    public void addEpicTasksIds(int id) {
        this.epicTasksId.add(id);
    }

    public void removeSubtaskId(int id) {
        this.epicTasksId.remove((Integer)id);
    }

    @Override
    public String toString() {
        return "EpicTask{\n" +
                " ID = " + getId() + ",\n" +
                " name = '" + getName() + "',\n" +
                " description = '" + getDescription() + "',\n" +
                " status = " + getStatus() + "',\n" +
                '}';
    }
}