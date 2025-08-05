package data;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String name, String description, int epicId) {
        super(id, name, description, Status.NEW);
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
                " nam = '" + getName() + ",\n" +
                " description = '" + getDescription() + ",\n" +
                " status = " + getStatus() + ",\n" +
                '}';
    }
}