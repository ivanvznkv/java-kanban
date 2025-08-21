package service;

import data.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(new Task(task));
        if (history.size() > 10) {
            history.remove(0);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
