package manager;
// Создала абстарктный класс

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void manager() {
        taskManager = createTaskManager();
    }

    @Test
    public void addTaskTest() {
        Task task = new Task("Понедельник", "день тяжелый", LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addTask(task);
        int id = task.getIdTask();
        Task taskSave = taskManager.getByIdTask(id);

        assertNotNull(taskSave, "Задача не найдена.");
        assertEquals(task, taskSave, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addEpicTest() {
        Epic epic = new Epic("Четверг", "маленькая пятница");
        taskManager.addEpic(epic);
        int id = epic.getIdTask();
        Epic epicSave = taskManager.getByIdEpic(id);

        assertNotNull(epicSave, "Задача не найдена.");
        assertEquals(epic, epicSave);

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addSubtaskTest() {
        Epic epic = new Epic("Четверг", "маленькая пятница");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("утро", "проснуться", Status.IN_PROGRESS, epic.getIdTask(), LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("утро", "проснуться", Status.IN_PROGRESS, epic.getIdTask(), LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(6));
        taskManager.addSubtask(subtask2);
        int id = subtask.getIdTask();
        Subtask subtaskSave = taskManager.getByIdSubtask(id);

        assertNotNull(subtaskSave, "Задача не найдена.");
        assertEquals(subtask, subtaskSave);

        final List<Subtask> subtasks = taskManager.getSubtasksEpic(1);
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");

        assertEquals(subtask2.getStartTime().plus(subtask2.getDuration()), epic.getEndTime(),
                "Время окончания выполнения эпика не обновилось при добавлении подзадач");
    }

    // Обновление
    @Test
    public void updateTaskTest() {
        Task task = new Task("Ночь", "...", LocalDateTime.of(1994, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addTask(task);
        task = new Task("День", "...", LocalDateTime.of(1994, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.updateTask(task);

        assertNotNull(task, "Задача пустая");
        assertNotEquals("Ночь", task.getName(), "Задача не обновилась");
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("...", "спать");
        taskManager.addEpic(epic);
        epic = new Epic("...", "лунатить");
        taskManager.updateEpic(epic);

        assertNotNull(epic, "Задача пустая");
        assertNotEquals("спать", epic.getDescription());
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("Четверг", "маленькая пятница");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("...", "...", Status.IN_PROGRESS, epic.getIdTask(),
                LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask);
        LocalDateTime startTimeExpected = epic.getStartTime();
        LocalDateTime endTimeExpected = epic.getEndTime();
        Duration durationExpected = epic.getDuration();

        subtask.setStatus(Status.DONE);
        subtask.setStartTime(LocalDateTime.of(2024, 4, 13, 17, 12));
        subtask.setDuration(Duration.ofMinutes(34));
        taskManager.updateSubtask(subtask);

        assertNotNull(subtask, "Задача пустая");
        assertNotEquals(Status.IN_PROGRESS, subtask.getStatus());
        assertNotEquals(startTimeExpected, epic.getStartTime(), "Стартовое время не обновилось");
        assertNotEquals(endTimeExpected, epic.getEndTime(), "Время окончания не обновилось");
        assertNotEquals(durationExpected, epic.getDuration(), "Продолжительность выполнения не обновилась");

    }

    // Получение списка всех задач
    @Test
    public void getAllTasksTest() {
        Task task1 = new Task("Ночь", "...", LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addTask(task1);
        Task task2 = new Task("День", "...", LocalDateTime.of(2024, 4, 13, 15, 50), Duration.ofMinutes(2));
        taskManager.addTask(task2);

        List<Task> comparable = new ArrayList<>();
        comparable.add(task1);
        comparable.add(task2);

        List<Task> actual = taskManager.getAllTasks();

        assertEquals(comparable, actual, "Ошибка в возврате списка всех задач");
    }

    @Test
    public void getAllEpicsTest() {
        Epic epic1 = new Epic("Ночь", "...");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("День", "...");
        taskManager.addEpic(epic2);

        List<Epic> comparable = new ArrayList<>();
        comparable.add(epic1);
        comparable.add(epic2);

        List<Epic> actual = taskManager.getAllEpics();

        assertEquals(comparable, actual, "Ошибка в возврате списка всех эпиков");
    }

    @Test
    public void getAllSubtasksTest() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("...", "...", Status.DONE, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.NEW, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 14, 54), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);

        List<Subtask> comparable = new ArrayList<>();
        comparable.add(subtask1);
        comparable.add(subtask2);

        List<Subtask> actual = taskManager.getAllSubtasks();

        assertEquals(comparable, actual, "Ошибка в возврате списка всех подзадач");
    }

    // Вывод подзадач конктретного эпика
    @Test
    public void getSubtasksEpicTest() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("...", "...", Status.DONE, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.NEW, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 14, 54), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);

        List<Subtask> comparable = new ArrayList<>();
        comparable.add(subtask1);
        comparable.add(subtask2);

        List<Subtask> actual = taskManager.getSubtasksEpic(epic.getIdTask());

        assertEquals(comparable, actual, "Ошибка в возврате списка подзадач по ID эпика");

    }

    // Получение по ID
    @Test
    public void getByIdTaskTest() {
        Task task1 = new Task("Ночь", "...", LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addTask(task1);
        Task task2 = new Task("День", "...", LocalDateTime.of(2024, 4, 13, 15, 50), Duration.ofMinutes(2));
        taskManager.addTask(task2);

        assertEquals(task2, taskManager.getByIdTask(task2.getIdTask()), "Ошибка при получении задачи по Id");

    }

    @Test
    public void getByIdEpicTest() {
        Epic epic1 = new Epic("Ночь", "...");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("День", "...");
        taskManager.addEpic(epic2);

        assertEquals(epic2, taskManager.getByIdEpic(epic2.getIdTask()), "Ошибка при получении эпика по Id");
    }

    @Test
    public void getByIdSubtask() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("...", "...", Status.DONE, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.NEW, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 14, 54), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);

        assertEquals(subtask2, taskManager.getByIdSubtask(subtask2.getIdTask()), "Ошибка при получении подзадачи по Id");
    }

    // Удаление всего
    @Test
    public void deleteTasksTest() {
        Task task1 = new Task("Ночь", "...", LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addTask(task1);
        Task task2 = new Task("День", "...", LocalDateTime.now().plusMinutes(49), Duration.ofMinutes(2));
        taskManager.addTask(task2);

        taskManager.deleteTasks();
        int expected2 = 0;
        int actual2 = taskManager.getAllTasks().size();
        assertEquals(expected2, actual2, "Все задачи не удалены");
    }

    @Test
    public void deleteEpicsTest() {
        Epic epic1 = new Epic("Ночь", "...");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("День", "...");
        taskManager.addEpic(epic2);

        taskManager.deleteEpics();
        int expected2 = 0;
        int actual2 = taskManager.getAllEpics().size();
        assertEquals(expected2, actual2, "Все задачи не удалены");
    }

    @Test
    public void deleteSubtasks() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("...", "...", Status.DONE, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.NEW, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 14, 54), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);

        assertEquals(subtask2.getStartTime().plus(subtask2.getDuration()), epic.getEndTime(),
                "Время окончания выполнения эпика не обновилось при добавлении подзадач");

        taskManager.deleteSubtasks();
        int expected2 = 0;
        int actual2 = taskManager.getAllSubtasks().size();
        assertEquals(expected2, actual2, "Все задачи не удалены");
        assertNull(epic.getStartTime(), "При удалении всех подзадач, сохраняется стартовое время эпика");
        assertEquals(Duration.ZERO, epic.getDuration(), "При удалении всех подзадач, сохраняется продолжительность эпика");
    }

    //Удаление по ID
    @Test
    public void deleteByIdTaskTest() {
        Task task1 = new Task("Ночь", "...", LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addTask(task1);
        int id = task1.getIdTask();
        Task task2 = new Task("День", "...", LocalDateTime.now().plusMinutes(49), Duration.ofMinutes(2));
        taskManager.addTask(task2);

        taskManager.deleteByIdTask(id);
        int expected = 1;
        int actual = taskManager.getAllTasks().size();
        assertEquals(expected, actual, "Задача по ID не удалена");
    }

    @Test
    public void deleteByIdEpicTest() {
        Epic epic1 = new Epic("Ночь", "...");
        taskManager.addEpic(epic1);
        int id = epic1.getIdTask();
        Epic epic2 = new Epic("День", "...");
        taskManager.addEpic(epic2);

        taskManager.deleteByIdEpic(id);
        int expected = 1;
        int actual = taskManager.getAllEpics().size();
        assertEquals(expected, actual, "Задача по ID не удалена");
    }

    @Test
    void deleteByIdSubtaskTest() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("...", "...", Status.DONE, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.NEW, epic.getIdTask(), LocalDateTime.of(2024, 4, 13, 14, 54), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);

        Integer idDelete = subtask2.getIdTask();
        ArrayList<Integer> test = epic.getSubtasks();
        boolean idInSubtaskList = test.contains(idDelete);
        assertTrue(idInSubtaskList, "Id подзадачи, которая будет удалена изначально не добавилась в список");

        taskManager.deleteByIdSubtask(subtask2.getIdTask());
        int expected = 1;
        int actual = taskManager.getAllSubtasks().size();
        assertEquals(expected, actual, "Задача по ID не удалена");
        assertEquals(subtask1.getStartTime().plus(subtask1.getDuration()), epic.getEndTime(),
                "Время окончания выполнения эпика не обновилось при удалении подзадачи");
        assertEquals(subtask1.getDuration(), epic.getDuration(),
                "Продолжительность выполнения эпика не обновилось при удалении подзадачи");

        ArrayList<Integer> test2 = epic.getSubtasks();
        boolean idInSubtaskList2 = test2.contains(idDelete);
        assertFalse(idInSubtaskList2, "Id удвленной подзадачи осталось в списке эпика");
    }

    @Test
    public void getHistoryTest() {
        Task task1 = new Task("Кот", 0, "...", LocalDateTime.of(2024, 12, 31, 14, 50), Duration.ofMinutes(25));
        taskManager.addTask(task1);
        Task task2 = new Task("Собака", "...", LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addTask(task2);
        Epic epic = new Epic("...", "...");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Хомяк", "...", Status.NEW, epic.getIdTask(), LocalDateTime.of(2024, 12, 31, 9, 12), Duration.ofMinutes(22));
        taskManager.addSubtask(subtask);

        taskManager.getByIdTask(task1.getIdTask());
        taskManager.getByIdTask(task2.getIdTask());
        taskManager.getByIdEpic(epic.getIdTask());
        taskManager.getByIdSubtask(subtask.getIdTask());

        List<Task> expected = new ArrayList<>();
        expected.add(task1);
        expected.add(task2);
        expected.add(epic);
        expected.add(subtask);

        assertEquals(expected, taskManager.getHistory());
    }

    //Приоритетность задач
    @Test
    public void getPrioritizedTasks() {
        Task task3 = new Task("Кот", 0, "...", LocalDateTime.of(2024, 12, 31, 14, 50), Duration.ofMinutes(25));
        taskManager.addTask(task3);
        Task task1 = new Task("Собака", "...", LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addTask(task1);
        Epic epic = new Epic("...", "...");
        taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("Хомяк", "...", Status.NEW, epic.getIdTask(), LocalDateTime.of(2024, 12, 31, 9, 12), Duration.ofMinutes(22));
        taskManager.addSubtask(subtask2);

        List<Task> expected = new ArrayList<>();
        expected.add(task1);
        expected.add(subtask2);
        expected.add(task3);

        List<Task> sortTasks = taskManager.getPrioritizedTasks();
        assertEquals(expected, sortTasks, "Ошибка сортировки при добавлении задач");

        taskManager.deleteByIdTask(task1.getIdTask());
        sortTasks = taskManager.getPrioritizedTasks();
        expected.removeFirst();
        assertEquals(expected, sortTasks, "Ошибка сортировки при удалении задач по Id");

        taskManager.deleteEpics();
        sortTasks = taskManager.getPrioritizedTasks();
        expected.removeFirst();
        assertEquals(expected, sortTasks, "Ошибка сохранения подзадач при удалении всех эпиков");

        Epic epic2 = new Epic("...", "...");
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Хомяк", "...", Status.NEW, epic2.getIdTask(), LocalDateTime.of(2024, 1, 9, 9, 7), Duration.ofMinutes(22));
        taskManager.addSubtask(subtask1);

        expected.addFirst(subtask1);
        taskManager.deleteByIdEpic(epic2.getIdTask());
        sortTasks = taskManager.getPrioritizedTasks();
        assertEquals(expected, sortTasks, "Ошибка сохранения подзадач при удалени эпиков по id");
    }
}


