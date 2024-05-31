import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Main metod = new Main();
        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "...");
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "...");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Эпик1", "...");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "...", Status.NEW, epic1.getIdTask());
        Subtask subtask2 = new Subtask("Подзадача 2", "...", Status.NEW, epic1.getIdTask());
        Subtask subtask3 = new Subtask("Подзадача 3", "...", Status.NEW, epic1.getIdTask());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Эпик2", "...");
        taskManager.addEpic(epic2);

        taskManager.getByIdTask(task1.getIdTask());
        taskManager.getByIdTask(task2.getIdTask());
        taskManager.getByIdEpic(epic1.getIdTask());
        taskManager.getByIdSubtask(subtask3.getIdTask());
        taskManager.getByIdEpic(epic2.getIdTask());
        taskManager.getByIdSubtask(subtask1.getIdTask());
        taskManager.getByIdSubtask(subtask2.getIdTask());

        List<Task> list = taskManager.getHistory();
        metod.print(list);

        taskManager.getByIdTask(task1.getIdTask());
        list = taskManager.getHistory();
        metod.print(list); //повторов нет

        taskManager.deleteByIdTask(task2.getIdTask());

        list = taskManager.getHistory();
        metod.print(list); // задача исчезла из истории

        taskManager.deleteByIdEpic(epic1.getIdTask());
        list = taskManager.getHistory();
        metod.print(list); // подзадачи исчезли вместе с эпиком

    }

    public void print(List<Task> list) {
        List<Task> history = new ArrayList<>();
        history = list;
        for (Task task : history) {
            System.out.println(task);
        }
        System.out.println("  ");
    }
}

