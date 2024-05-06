package Test.Manager;


import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void manager() {
        taskManager = Managers.getDefaultTask();
    }
    //Тест-проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи, не реализован
    // так как это невозможно в силу реализации кода

    //Проверяем, что объект Subtask нельзя сделать своим же эпиком
    @Test
    public void SubtaskCanNotBeEpic() {
        Subtask subtask = new Subtask("Подзадача", "...", Status.NEW, 1);
        assertNull(subtask.getIdTask());
    }

    //Проверяем, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    public void AddNewTask() {
        Task task = new Task("Понедельник", "день тяжелый");
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
    public void AddNewEpic() {
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
    public void AddNewSubtask() {
        Epic epic = new Epic("Четверг", "маленькая пятница");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("утро", "проснуться", Status.IN_PROGRESS, epic.getIdTask());
        taskManager.addSubtask(subtask);
        int id = subtask.getIdTask();
        Subtask subtaskSave = taskManager.getByIdSubtask(id);

        assertNotNull(subtaskSave, "Задача не найдена.");
        assertEquals(subtask, subtaskSave);

        final List<Subtask> subtasks = taskManager.getSubtasksEpic(0);
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    //Проверяем, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    // даже если пользователь по ошибке воспользуется конструктором с id, в мапу он пойдет с сгенирирвоанным id
    @Test
    public void NoConflictBetweenTasksWithGeneratedAndPreassignedIDs() {
        Task taskPreassigned = new Task("Кот", 0, "...");
        taskManager.addTask(taskPreassigned);
        Task taskGenerate = new Task("Собака", "...");
        taskManager.addTask(taskGenerate);

        int idTaskPreassigned = taskPreassigned.getIdTask();
        int idTaskGenerate = taskGenerate.getIdTask();

        int expected = 2;
        int actual = taskManager.getAllTasks().size();
        assertEquals(expected, actual, "Одна из задач отсутсвует");
        assertNotEquals(idTaskPreassigned, idTaskGenerate, "ID совпадают, конфликт");
    }

    //Проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void constancyOfTheTaskAfterAddManager() {
        Task task = new Task("Понедельник", "день тяжелый");
        taskManager.addTask(task);
        String name = "Понедельник";
        String nameAfter = task.getName();
        String description = "день тяжелый";
        String descriptionAfter = task.getDescription();
        int id = 0;
        int idAfter = task.getIdTask();
        Status status = Status.NEW;
        Status statusAfter = task.getStatus();

        assertEquals(name, nameAfter, "Имена задач не совпадают");
        assertEquals(description, descriptionAfter, "Описание задач не совпадают");
        assertEquals(id, idAfter, "ID задач не совпадают");
        assertEquals(status, statusAfter, "Статус задач не совпадают");
    }
}

