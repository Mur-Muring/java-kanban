package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int historyListSize=10;
    private static final List<Task>history=new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null){
            return;
        }
        if (history.size()<historyListSize){
            history.add(task);
        } else {
            history.removeFirst();
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory(){
            return new ArrayList<>(history);
        }
}
