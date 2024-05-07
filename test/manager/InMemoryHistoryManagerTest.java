package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
}