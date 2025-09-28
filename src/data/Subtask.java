package data;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description, TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{\n" +
                " ID = " + getId() + ",\n" +
                " name = '" + getName() + "',\n" +
                " description = '" + getDescription() + "',\n" +
                " status = '" + getStatus() + "',\n" +
                '}';
    }
}
