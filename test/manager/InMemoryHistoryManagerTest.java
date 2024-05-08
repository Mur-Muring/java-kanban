// Переделала тест на проверку не более 10 историй. Спасибо, Ваш вариант гораздо лучше.
// Проверку на null вынесла в отельный тест, возможно это повляет на вреся обработки,но мы еще не проходили
// и надеюсь это не будет ошибкой

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
        for (int i = 0; i < 11; i++) {
            historyManager.add(new Task("...", "..."));
        }
        Assertions.assertEquals(10, historyManager.getHistory().size(), "В истории храниться больше 10 позтций");
    }

    @Test
    public void addTaskNullTest() {
        for (int i = 0; i < 4; i++) {
            historyManager.add(new Task("...", "..."));
        }
        Task taskNull = null;
        historyManager.add(taskNull);
        Assertions.assertEquals(4, historyManager.getHistory().size(), "В историю пробралась пустая задача");
    }
}