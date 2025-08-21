package data;

import java.util.ArrayList;
import java.util.Iterator;


public class Epic extends Task {
    private ArrayList<Integer> epicId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getEpicsId() {
        return new ArrayList<>(epicId); // возвращаем копию
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