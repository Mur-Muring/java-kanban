/*
1. Добавила тесты на порядок задач после удаления из истории
 */
package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


class InMemoryHistoryManagerTest {

    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    public void testManager() {
        manager = Managers.getDefaultTask();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void taskDoesNotChangeInHistory() {
        Task task = manager.addTask(new Task("Задача", "555"));
        manager.getByIdTask(task.getIdTask());
        task.setName("Измененная задача");
        manager.updateTask(task);

        List<Task> history = historyManager.getHistory();
        for (Task historyTask : history) {
            Assertions.assertNotEquals(manager.getByIdTask(task.getIdTask()).getName(), historyTask.getName());
        }
    }

    @Test
    public void epicDoesNotChangeInHistory() {
        Epic epic = manager.addEpic(new Epic("Эпик", "..."));
        manager.getByIdEpic(epic.getIdTask());
        epic.setName("Измененное имя");
        manager.updateEpic(epic);

        List<Task> history = historyManager.getHistory();
        for (Task historyTask : history) {
            Assertions.assertNotEquals(manager.getByIdEpic(epic.getIdTask()).getName(), historyTask.getName());
        }
    }

    @Test
    public void subtaskDoesNotChangeInHistory() {
        Epic epic = manager.addEpic(new Epic("Эпик", "..."));
        Subtask subtask = manager.addSubtask(new Subtask("Подзадача", "...", Status.DONE, epic.getIdTask()));
        manager.getByIdSubtask(subtask.getIdTask());
        subtask.setName("Новое имя");
        manager.updateSubtask(subtask);

        List<Task> history = historyManager.getHistory();
        for (Task historyTask : history) {
            Assertions.assertNotEquals(manager.getByIdSubtask(subtask.getIdTask()).getName(), historyTask.getName());
        }
    }

    @Test
    public void addTaskTest() {
        Task task1 = new Task("1", 0, "www");
        Task task2 = new Task("2", 1, "www");
        Task task3 = new Task("3", 2, "www");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        Assertions.assertEquals(3, historyManager.getHistory().size(), "Не все задачи попали в историю");

        task2.setDescription("eeee");
        historyManager.add(task2);

        Assertions.assertEquals(3, historyManager.getHistory().size(), "Предыдущий просмотр задачи не был удален");

        Task last = historyManager.getHistory().getLast();
        Assertions.assertEquals(last, task2, "Задача после повторного добавления не ушла в конец списка");

        Task taskNull = null;
        historyManager.add(taskNull);
        Assertions.assertEquals(3, historyManager.getHistory().size(), "В историю пробралась пустая задача");
    }

    @Test
    public void deleteByIdTest() {
        Task task1 = new Task("Задача 1", 0, "www");
        Task task2 = new Task("Задача 2", 1, "www");
        Task task3 = new Task("Задача 3", 2, "www");
        Task task4 = new Task("Задача 4", 3, "www");
        Task task5 = new Task("Задача 5", 4, "www");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);

        int idDelete = task3.getIdTask();
        historyManager.remove(idDelete);
        Assertions.assertEquals(historyManager.getHistory().get(idDelete - 1), task2,
                "Порядок списка нарушен(удаление из середины");
        Assertions.assertEquals(historyManager.getHistory().get(idDelete), task4,
                "Порядок списка нарушен(удаление из середины");

        Assertions.assertEquals(historyManager.getHistory().getFirst(), task1,
                "Первая добавленная задача не первая в списке");
        historyManager.remove(task1.getIdTask());
        Assertions.assertEquals(historyManager.getHistory().getFirst(), task2,
                "Порядок списка нарушен(удаление из начала");

        Assertions.assertEquals(historyManager.getHistory().getLast(), task5,
                "Последняя добавленная задача не последняя в списке");
        historyManager.remove(task5.getIdTask());
        Assertions.assertEquals(historyManager.getHistory().getLast(), task4,
                "Порядок списка нарушен(удаление из конца");
    }
}

