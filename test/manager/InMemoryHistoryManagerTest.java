// Добавила тест на хранение не больше 10 задач

package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManagerTest {

    private TaskManager manager;

    @BeforeEach
    public void testManager() {
        manager = Managers.getDefaultTask();
    }

    @Test
    public void taskDoesNotChangeInHistory() {
        Task task = manager.addTask(new Task("Задача", "555"));
        manager.getByIdTask(task.getIdTask());
        task.setName("Измененная задача");
        manager.updateTask(task);

        Assertions.assertNotEquals(manager.getHistory().getFirst().getName(),
                manager.getByIdTask(task.getIdTask()).getName());
    }

    @Test
    public void epicDoesNotChangeInHistory() {
        Epic epic = manager.addEpic(new Epic("Эпик", "..."));
        manager.getByIdEpic(epic.getIdTask());
        epic.setName("Измененное имя");
        manager.updateEpic(epic);

        Assertions.assertNotEquals(manager.getHistory().getFirst().getName(),
                manager.getByIdEpic(epic.getIdTask()).getName());
    }

    @Test
    public void subtaskDoesNotChangeInHistory() {
        Epic epic = manager.addEpic(new Epic("Эпик", "..."));
        Subtask subtask = manager.addSubtask(new Subtask("Подзадача", "...", Status.DONE, epic.getIdTask()));
        manager.getByIdSubtask(subtask.getIdTask());
        subtask.setName("Новое имя");
        manager.updateSubtask(subtask);

        Assertions.assertNotEquals(manager.getHistory().getFirst().getName(),
                manager.getByIdSubtask(subtask.getIdTask()).getName());
    }

    @Test
    public void addTaskTest() {
        Task task = null;
        Task task1 = new Task("...", "...");
        Task task2 = new Task("...", "...");
        Task task3 = new Task("...", "...");
        Task task4 = new Task("...", "...");
        Task task5 = new Task("...", "...");
        Task task6 = new Task("...", "...");
        Task task7 = new Task("...", "...");
        Task task8 = new Task("...", "...");
        Task task9 = new Task("...", "...");
        Task task10 = new Task("...", "...");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        manager.addTask(task5);
        manager.addTask(task6);
        manager.addTask(task7);
        manager.addTask(task8);
        manager.addTask(task9);
        manager.addTask(task10);
        manager.addTask(task);

        List<Task> saveTasks = new ArrayList<>();
        saveTasks.add(task);
        saveTasks.add(task1);
        saveTasks.add(task2);
        saveTasks.add(task3);
        saveTasks.add(task4);
        saveTasks.add(task5);
        saveTasks.add(task6);
        saveTasks.add(task7);
        saveTasks.add(task8);
        saveTasks.add(task9);
        saveTasks.add(task10);

        for (int i = 0; i < saveTasks.size(); i++) {
            manager.getByIdTask(i);
        }
        Assertions.assertNotEquals(manager.getHistory(), saveTasks, "Добавлен null");

        Task task11 = new Task("11", "...");
        manager.addTask(task11);
        saveTasks.add(task11);
        int idTask11 = task11.getIdTask();
        manager.getByIdTask(idTask11);

        Assertions.assertEquals(10, manager.getHistory().size(), "В истории больше 10 позиций");
    }
}