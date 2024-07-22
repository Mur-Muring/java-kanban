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
3. Добавила тесты на валидность и конфликты по времени в выполнении задачи
4. Расширирала тест на проверку статуса, согласно заданию
 */

import exception.TimeConflictException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    //Проверяем, что объект Subtask нельзя сделать своим же эпиком
    @Test
    public void subtaskCanNotBeEpic() {
        Subtask subtask = new Subtask("Подзадача", "...", Status.NEW, 1, LocalDateTime.of(1994, 4, 13, 11, 50), Duration.ofMinutes(2));
        assertNull(subtask.getIdTask());
    }

    //Проверяем, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    // даже если пользователь по ошибке воспользуется конструктором с id, в мапу он пойдет с сгенирирвоанным id
    @Test
    public void noConflictBetweenTasksWithGeneratedAndPreassignedIDs() {
        Task taskPreassigned = new Task("Кот", 0, "...", LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addTask(taskPreassigned);
        Task taskGenerate = new Task("Собака", "...", LocalDateTime.now().plusHours(1), Duration.ofMinutes(2));
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
        Task task = new Task("Понедельник", "день тяжелый", LocalDateTime.of(2024, 4, 13, 11, 50), Duration.ofMinutes(2));
        taskManager.addTask(task);
        String name = "Понедельник";
        String nameAfter = task.getName();
        String description = "день тяжелый";
        String descriptionAfter = task.getDescription();
        int id = 1;
        int idAfter = task.getIdTask();
        Status status = Status.NEW;
        Status statusAfter = task.getStatus();
        LocalDateTime startTime = LocalDateTime.of(2024, 4, 13, 11, 50);
        LocalDateTime startTimeAfter = task.getStartTime();
        Duration duration = Duration.ofMinutes(2);
        Duration durationAfter = task.getDuration();

        assertEquals(name, nameAfter, "Имена задач не совпадают");
        assertEquals(description, descriptionAfter, "Описание задач не совпадают");
        assertEquals(id, idAfter, "ID задач не совпадают");
        assertEquals(status, statusAfter, "Статус задач не совпадают");
        assertEquals(startTime, startTimeAfter, "Старт задач не совпадает");
        assertEquals(duration, durationAfter, "Продолжительность задач не совпадает");
    }

    @Test
    public void updateStatusTest() {
        Epic epic = new Epic("Ночь", "...");
        taskManager.addEpic(epic);
        Status status = epic.getStatus();
        Subtask subtask1 = new Subtask("...", "...", Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("...", "...", Status.NEW, epic.getIdTask(), LocalDateTime.now().plusHours(1), Duration.ofMinutes(2));
        taskManager.addSubtask(subtask2);

        assertEquals(status, Status.NEW, "Нарушено условие все позадачи имеют статус NEW");

        taskManager.subtasks.get(subtask1.getIdTask()).setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.subtasks.get(subtask2.getIdTask()).setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        status = epic.getStatus();

        assertEquals(status, Status.DONE, "Нарушено условие все позадачи имеют статус DONE");

        taskManager.subtasks.get(subtask2.getIdTask()).setStatus(Status.NEW);
        taskManager.updateSubtask(subtask2);
        status = epic.getStatus();
        assertEquals(status, Status.IN_PROGRESS, "Нарушено условие позадачи имеют статус DONE и NEW");

        taskManager.subtasks.get(subtask1.getIdTask()).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.subtasks.get(subtask2.getIdTask()).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        status = epic.getStatus();

        assertEquals(status, Status.IN_PROGRESS, "Нарушено условие все позадачи имеют статус IN_PROGRESS");
    }

    // Проверяем вляет ли setter на работу менеджера
    @Test
    public void checkImmutabilityOfTaskWhenCreatedValueChanged() {
        Task created = taskManager.addTask(new Task("1", "www", LocalDateTime.now(), Duration.ofMinutes(2)));
        Task returned = taskManager.getByIdTask(created.getIdTask());
        Task change = new Task(returned);
        change.setName("2");
        assertNotEquals(change.getName(), taskManager.getByIdTask(created.getIdTask()).getName());
    }

    @Test
    public void checkImmutabilityOfTaskWhenSourceChanged() {
        Task sourse = new Task("1", "www", LocalDateTime.now(), Duration.ofMinutes(2));
        Task created = new Task(sourse.getName(), sourse.getDescription(), sourse.getStartTime(), sourse.getDuration());
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
                created.getStartTime(), created.getDuration());
        change.setName("2");
        assertNotEquals(change.getName(), taskManager.getByIdSubtask(created.getIdTask()).getName());
    }

    @Test
    public void checkImmutabilityOfSubtaskWhenSourceChanged() {
        Epic epic = taskManager.addEpic(new Epic("...", "www"));
        Subtask source = new Subtask("1", "www", Status.NEW, epic.getIdTask(), LocalDateTime.now(), Duration.ofMinutes(2));
        Subtask created = new Subtask(source.getName(), source.getDescription(), source.getStatus(), source.getIdEpic(), source.getStartTime(), source.getDuration());
        taskManager.addSubtask(created);
        source.setName("2");
        assertNotEquals(source.getName(), taskManager.getByIdSubtask(created.getIdTask()).getName());
    }

    // тест на конфликт времени

    @Test
    public void timeConflictTest() {
        Task task1 = new Task("Кот", 0, "...", LocalDateTime.now(), Duration.ofMinutes(25));
        Task task2 = new Task("Собака", "...", LocalDateTime.now().plusHours(1), Duration.ofMinutes(2));

        assertDoesNotThrow(() -> taskManager.addTask(task1), "Ошибка во времени при добавлении задачи");
        assertDoesNotThrow(() -> taskManager.addTask(task2), "Конфликт в задачах с разным временем");

        List<Task> tasks = taskManager.getPrioritizedTasks();
        assertEquals(2, tasks.size(), "Возник конфликт времени, задачи не добавлены в список");

        Epic epic = new Epic("...", "...");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Кот", "...", Status.NEW, epic.getIdTask(),
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(125));
        Subtask subtask2 = new Subtask("Собака", "...", Status.DONE, epic.getIdTask(),
                LocalDateTime.now().plusHours(3), Duration.ofMinutes(32));

        assertDoesNotThrow(() -> taskManager.addSubtask(subtask), "Ошибка во времени при добавлении подзадачи");
        assertThrows(TimeConflictException.class, () -> taskManager.addSubtask(subtask2), "Добавляются подзадачи с конфликтом времени");

        List<Task> tasks2 = taskManager.getPrioritizedTasks();
        assertEquals(3, tasks2.size(), "Добавляются задачи с конфликтом времени");
    }

    @Test
    public void inValidForTimeTest() {
        Task invalidTask = new Task("Кот", 0, "...", LocalDateTime.now(), Duration.ofMinutes(-60));
        Epic epic = new Epic("...", "...");
        taskManager.addEpic(epic);
        Subtask invalidSubtask = new Subtask("Кот", "...", Status.NEW, epic.getIdTask(),
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(-300));

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(invalidTask), "Добавилась невалидная задача");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addSubtask(invalidSubtask), "Добавилась невалидная подзадача");

        List<Task> tasks = taskManager.getPrioritizedTasks();
        assertTrue(tasks.isEmpty(), "Список не пуст, добавилась невалижная задача");

    }
}

