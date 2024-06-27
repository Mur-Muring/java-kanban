/*
1. Все работает! Моя ошибка была в том, что добавив в метод удаления историию, я сломала тесты пустой нодой, решение
как раз было в проверке на null в методе removeNode, а я заставила это работать гетом и только потом добавила проверку в
removeNode.
Сейчас я убрала геты и все работает без них.
 */
package manager;
/*
1. Обновила методы, проверила как сохраняктся время и продолжительность для задач, и как пересчитывается для
эпиков при удалении подзадач
2. Добавила тест на проверку сортировки задач и подзадач по времени
 */

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
    public void subtaskCanNotBeEpic() {
        Subtask subtask = new Subtask("Подзадача", "...", Status.NEW, 1, LocalDateTime.of(1994,4,13,11,50), Duration.ofMinutes(2));
        assertNull(subtask.getIdTask());
    }

    //Проверяем, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    public void addNewTask() {
        Task task = new Task("Понедельник", "день тяжелый",LocalDateTime.now(), Duration.ofMinutes(2));
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
    public void addNewEpic() {
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
    public void addNewSubtask() {
        Epic epic = new Epic("Четверг", "маленькая пятница");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("утро", "проснуться", Status.IN_PROGRESS, epic.getIdTask(),LocalDateTime.now(), Duration.ofMinutes(2));
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
    public void noConflictBetweenTasksWithGeneratedAndPreassignedIDs() {
        Task taskPreassigned = new Task("Кот", 0, "...",LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addTask(taskPreassigned);
        Task taskGenerate = new Task("Собака", "...",LocalDateTime.now(), Duration.ofMinutes(2));
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
        Task task = new Task("Понедельник", "день тяжелый",LocalDateTime.of(2024,4,13,11,50), Duration.ofMinutes(2));
        taskManager.addTask(task);
        String name = "Понедельник";
        String nameAfter = task.getName();
        String description = "день тяжелый";
        String descriptionAfter = task.getDescription();
        int id = 0;
        int idAfter = task.getIdTask();
        Status status = Status.NEW;
        Status statusAfter = task.getStatus();
        LocalDateTime startTime=LocalDateTime.of(2024,4,13,11,50);
        LocalDateTime startTimeAfter=task.getStartTime();
        Duration duration=Duration.ofMinutes(2);
        Duration durationAfter=task.getDuration();

        assertEquals(name, nameAfter, "Имена задач не совпадают");
        assertEquals(description, descriptionAfter, "Описание задач не совпадают");
        assertEquals(id, idAfter, "ID задач не совпадают");
        assertEquals(status, statusAfter, "Статус задач не совпадают");
    assertEquals(startTime,startTimeAfter,"Старт задач не совпадает");
        assertEquals(duration,durationAfter,"Продолжительность задач не совпадает");


    }

    // Проверяем, что задачи обновляются
    @Test
    public void updateTaskTest() {
        Task task = new Task("Ночь", "...",LocalDateTime.of(1994,4,13,11,50), Duration.ofMinutes(2));
        taskManager.addTask(task);
        task = new Task("День", "...",LocalDateTime.of(1994,4,13,11,50), Duration.ofMinutes(2));
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
        Subtask subtask = new Subtask("...", "...", Status.IN_PROGRESS, 1,
                LocalDateTime.of(1994,4,13,11,50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask);
        subtask = new Subtask("...", "...", Status.DONE, 1,
                LocalDateTime.of(1994,4,13,11,50), Duration.ofMinutes(2));
        taskManager.updateSubtask(subtask);

        assertNotNull(subtask, "Задача пустая");
        assertNotEquals(Status.IN_PROGRESS, subtask.getStatus());
    }

    // Проверяем, что задачи удаляются
    @Test
    public void deleteTaskTest() {
        Task task1 = new Task("Ночь", "...",LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addTask(task1);
        int id = task1.getIdTask();
        Task task2 = new Task("День", "...",LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addTask(task2);

        taskManager.deleteByIdTask(id);
        int expected = 1;
        int actual = taskManager.getAllTasks().size();
        assertEquals(expected, actual, "Задача по ID не удалена");

        taskManager.deleteTasks();
        int expected2 = 0;
        int actual2 = taskManager.getAllTasks().size();
        assertEquals(expected2, actual2, "Все задачи не удалены");
    }

    @Test
    public void deleteEpicTest() {
        Epic epic1 = new Epic("Ночь", "...");
        taskManager.addEpic(epic1);
        int id = epic1.getIdTask();
        Epic epic2 = new Epic("День", "...");
        taskManager.addEpic(epic2);

        taskManager.deleteByIdEpic(id);
        int expected = 1;
        int actual = taskManager.getAllEpics().size();
        assertEquals(expected, actual, "Задача по ID не удалена");

        taskManager.deleteEpics();
        int expected2 = 0;
        int actual2 = taskManager.getAllEpics().size();
        assertEquals(expected2, actual2, "Все задачи не удалены");
    }

    @Test
    public void deleteSubtaskTest() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("...", "...", Status.DONE, 0,LocalDateTime.of(2024,4,13,11,50), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.NEW, 0,LocalDateTime.of(2024,4,13,14,54), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);

        Integer idDelete = subtask2.getIdTask();
        ArrayList<Integer> test = epic.getSubtasks();
        boolean idInSubtaskList = test.contains(idDelete);
        assertTrue(idInSubtaskList, "Id подзадачи, которая будет удалена изначально не добавилась в список");

        assertEquals(subtask2.getStartTime().plus(subtask2.getDuration()),epic.getEndTime(),
                "Время окончания выполнения эпика не обновилось при добавлении подзадач");

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

        taskManager.deleteSubtasks();
        int expected2 = 0;
        int actual2 = taskManager.getAllSubtasks().size();
        assertEquals(expected2, actual2, "Все задачи не удалены");
        assertNull(epic.getStartTime(),"При удалении всех подзадач, сохраняется стартовое время эпика");
        assertEquals(Duration.ZERO, epic.getDuration(), "При удалении всех подзадач, сохраняется продолжительность эпика");
    }

    @Test
    public void updateStatusTest() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Status status1 = epic.getStatus();
        Subtask subtask1 = new Subtask("...", "...", Status.DONE, 0,LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.DONE, 0,LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);
        Status status2 = epic.getStatus();

        assertNotEquals(status1, status2, "Статус не обновился");
    }

    // Проверяем вляет ли setter на работу менеджера
    @Test
    public void checkImmutabilityOfTaskWhenCreatedValueChanged() {
        Task created = taskManager.addTask(new Task("1", "www",LocalDateTime.now(), Duration.ofMinutes(2)));
        Task returned = taskManager.getByIdTask(created.getIdTask());
        Task change = new Task(returned);
        change.setName("2");
        assertNotEquals(change.getName(), taskManager.getByIdTask(created.getIdTask()).getName());
    }

    @Test
    public void checkImmutabilityOfTaskWhenSourceChanged() {
        Task sourse = new Task("1", "www",LocalDateTime.now(), Duration.ofMinutes(2));
        Task created = new Task(sourse.getName(), sourse.getDescription(),sourse.getStartTime(),sourse.getDuration());
        taskManager.addTask(created);
        sourse.setName("2");
        assertNotEquals(sourse.getName(), taskManager.getByIdTask(created.getIdTask()).getName());
    }

    @Test
    public void checkImmutabilityOfEpicWhenCreatedValueChanged() {
        Epic created = taskManager.addEpic(new Epic("1", "www"));
        Epic change = new Epic(created.getName(), created.getDescription());
        change.setName("2");
        assertNotEquals(change.getName(), taskManager.getByIdEpic(created.getIdTask()).getName());
    }

    @Test
    public void checkImmutabilityOfEpicWhenSourceChanged() {
        Epic source = new Epic("1", "www");
        Epic created = new Epic(source.getName(), source.getDescription());
        taskManager.addEpic(created);
        source.setName("2");
        assertNotEquals(source.getName(), taskManager.getByIdEpic(created.getIdTask()).getName());
    }

    @Test
    public void checkImmutabilityOfSubtaskWhenCreatedValueChanged() {
        Epic epic = taskManager.addEpic(new Epic("...", "www"));
        Subtask created = taskManager.addSubtask(new Subtask("1", "www", Status.NEW, epic.getIdTask(),
                LocalDateTime.now(), Duration.ofMinutes(2)));
        Subtask change = new Subtask(created.getName(), created.getDescription(), created.getStatus(), created.getIdEpic(),
                created.getStartTime(),created.getDuration());
        change.setName("2");
        assertNotEquals(change.getName(), taskManager.getByIdSubtask(created.getIdTask()).getName());
    }

    @Test
    public void checkImmutabilityOfSubtaskWhenSourceChanged() {
        Epic epic = taskManager.addEpic(new Epic("...", "www"));
        Subtask source = new Subtask("1", "www", Status.NEW, epic.getIdTask(),LocalDateTime.now(), Duration.ofMinutes(2));
        Subtask created = new Subtask(source.getName(), source.getDescription(), source.getStatus(), source.getIdEpic(), source.getStartTime(), source.getDuration());
        taskManager.addSubtask(created);
        source.setName("2");
        assertNotEquals(source.getName(), taskManager.getByIdSubtask(created.getIdTask()).getName());
    }

    // тест на сортровку по времени и добавления уникальных задач и позадач
    @Test
    public void prioritizedTaskTest(){
        //Я помню, что использовать цифры в названии переменных дурной тон, но это был способ не запутаться в очередности самой
        Task task3 = new Task("Кот", 0, "...",LocalDateTime.of(2024,12,31,14,50), Duration.ofMinutes(25));
        taskManager.addTask(task3);
        Task task1= new Task("Собака", "...",LocalDateTime.of(2024,4,13,11,50), Duration.ofMinutes(2));
        taskManager.addTask(task1);
        Epic epic=new Epic("...","...");
        taskManager.addEpic(epic);
        Subtask subtask2=new Subtask("Хомяк","...", Status.NEW,epic.getIdTask(),LocalDateTime.of(2024,12,31,9,12),Duration.ofMinutes(22));
        taskManager.addSubtask(subtask2);

        List<Task> expected=new ArrayList<>();
        expected.add(task1);
        expected.add(subtask2);
        expected.add(task3);

        List<Task> sortTasks=taskManager.getPrioritizedTasks();
        assertEquals(expected,sortTasks,"Ошибка сортировки при добавлении задач");

        taskManager.deleteByIdTask(task1.getIdTask());
        sortTasks=taskManager.getPrioritizedTasks();
        expected.removeFirst();
        assertEquals(expected,sortTasks,"Ошибка сортировки при удалении задач по Id");

        taskManager.deleteEpics();
        sortTasks=taskManager.getPrioritizedTasks();
        expected.removeFirst();
        assertEquals(expected,sortTasks,"Ошибка сохранения подзадач при удалении всех эпиков");

        Epic epic2=new Epic("...","...");
        taskManager.addEpic(epic2);
        Subtask subtask1=new Subtask("Хомяк","...", Status.NEW,epic2.getIdTask(),LocalDateTime.of(2024,1,9,9,7),Duration.ofMinutes(22));
        taskManager.addSubtask(subtask1);

        expected.addFirst(subtask1);
        taskManager.deleteByIdEpic(epic2.getIdTask());
        sortTasks=taskManager.getPrioritizedTasks();
        assertEquals(expected,sortTasks,"Ошибка сохранения подзадач при удалени эпиков по id");
    }
}

