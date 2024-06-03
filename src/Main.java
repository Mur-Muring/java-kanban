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

        System.out.println("Отобразим историю с 2 задачами, 2 эпиками и 3 эпиками");
        List<Task> list = taskManager.getHistory();
        metod.print(list);

        System.out.println("После повторного просмотра первой задачи, первый просмотр был удален," +
                " а новый переместился в конец списка");
        taskManager.getByIdTask(task1.getIdTask());
        list = taskManager.getHistory();
        metod.print(list); //повторов нет

        taskManager.deleteByIdTask(task2.getIdTask());
        System.out.println("Удалили по Id Задачу 2");
        list = taskManager.getHistory();
        metod.print(list); // задача исчезла из истории

        taskManager.deleteByIdEpic(epic1.getIdTask());
        System.out.println("Удалили по Id Эпик 1, подзадачи исчезли вместе с эпиком");
        list = taskManager.getHistory();
        metod.print(list); // подзадачи исчезли вместе с эпиком


        Task task3 = new Task("Задача 3", "...");
        taskManager.addTask(task3);
        Task task4 = new Task("Задача 4", "...");
        taskManager.addTask(task4);

        Epic epic3 = new Epic("Эпик3", "...");
        taskManager.addEpic(epic3);
        Subtask subtask4 = new Subtask("Подзадача 3.1", "...", Status.NEW, epic3.getIdTask());
        Subtask subtask5 = new Subtask("Подзадача 3.2", "...", Status.NEW, epic3.getIdTask());
        Subtask subtask6 = new Subtask("Подзадача 3.3", "...", Status.NEW, epic3.getIdTask());
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);

        taskManager.getByIdTask(task3.getIdTask());
        taskManager.getByIdTask(task4.getIdTask());
        taskManager.getByIdEpic(epic3.getIdTask());
        taskManager.getByIdSubtask(subtask4.getIdTask());
        taskManager.getByIdSubtask(subtask5.getIdTask());
        taskManager.getByIdSubtask(subtask6.getIdTask());

        System.out.println("Наполнили историю новыми задачами, эриками, подзадачами");
        list = taskManager.getHistory();
        metod.print(list);

        taskManager.deleteTasks();
        System.out.println("Удалили все задачи");
        list = taskManager.getHistory();
        metod.print(list);

        System.out.println("Удалили все эпики, подзадачи тоже удадились, история пуста");
        taskManager.deleteEpics();
        list = taskManager.getHistory();
        metod.print(list);

        Epic epic4 = new Epic("Эпик4", "...");
        taskManager.addEpic(epic4);
        Subtask subtask7 = new Subtask("Подзадача 4.1", "...", Status.NEW, epic4.getIdTask());
        Subtask subtask8 = new Subtask("Подзадача 4.2", "...", Status.NEW, epic4.getIdTask());
        Subtask subtask9 = new Subtask("Подзадача 4.3", "...", Status.NEW, epic4.getIdTask());
        taskManager.addSubtask(subtask7);
        taskManager.addSubtask(subtask8);
        taskManager.addSubtask(subtask9);

        taskManager.getByIdEpic(epic4.getIdTask());
        taskManager.getByIdSubtask(subtask7.getIdTask());
        taskManager.getByIdSubtask(subtask8.getIdTask());
        taskManager.getByIdSubtask(subtask9.getIdTask());

        System.out.println("Наполнили историю Эпиком с подзадачами");
        list = taskManager.getHistory();
        metod.print(list);

        System.out.println("Удалили подзадачи");
        taskManager.deleteSubtasks();
        list = taskManager.getHistory();
        metod.print(list);


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

