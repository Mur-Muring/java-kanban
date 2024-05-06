package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int historyListSize = 10;
    private static final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() < historyListSize) {
                history.add(new Task(task));
            } else {
                history.removeFirst();
                history.add(new Task(task));
            }
        }
    }

    public List<Task> copyHistory() {
        List<Task> list = new ArrayList<>();
        for (Task task : history) {
            list.add(new Task(task));
        }
        return list;
    }

    @Override
    public List<Task> getHistory() {
        return copyHistory();
    }


}
