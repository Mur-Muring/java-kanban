/*
Имправления:
1. Правильное название static константы
2. Убрала static в объявлении списка историй
3. Переписала метод add()
4. Сделала метод copyHistory() приватным

 */
package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_LIST_SIZE = 10;
    private  final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() >= HISTORY_LIST_SIZE) {
                history.removeFirst();
            }
            history.add(new Task(task));
        }
    }

    private List<Task> copyHistory() {
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
