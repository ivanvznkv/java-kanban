package data;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> epicId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getEpicsId() {
        return epicId;
    }

    public void addEpicIds(int id) {
        this.epicId.add(id);
    }

    public void removeSubtaskId(int id) {
        this.epicId.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{\n" +
                " ID = " + getId() + ",\n" +
                " name = '" + getName() + "',\n" +
                " description = '" + getDescription() + "',\n" +
                " status = '" + getStatus() + "',\n" +
                '}';
    }
}