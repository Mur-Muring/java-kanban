import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task = new Task("Понедельник", "день тяжелый");
        taskManager.addTask(task);
        Task task1 = new Task("Вторник", "день рабочий");
        taskManager.addTask(task1);
        Task task2 = new Task("Среда", "день веселый");
        taskManager.addTask(task2);

        System.out.println("Посмотрим на понельник " + task);
        System.out.println("Посмотрим на вторник " + task1);
        System.out.println("Посмотрим на среду " + task2);

        Epic epic = new Epic("Четверг", "маленькая пятница");
        Epic epic1 = new Epic("Пятница", "день новой теории по спринту");
        Epic epic2 = new Epic("Суббота", "день прогулок");

        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        System.out.println("Посмотрим на четверг " + epic);
        System.out.println("Посмотрим на пятницу " + epic1);
        System.out.println("Посмотрим на субботу " + epic2);

        Subtask subtask = new Subtask("утро", "проснуться", Status.IN_PROGRESS, epic.getIdTask());
        Subtask subtask1 = new Subtask("день", "выжить", Status.NEW, epic1.getIdTask());
        Subtask subtask2 = new Subtask("вечер", "отдохнуть", Status.DONE, epic2.getIdTask());

        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.getSubtasksEpic(epic1);
        taskManager.getSubtasksEpic(epic1);
        taskManager.getSubtasksEpic(epic1);
        System.out.println(taskManager.getSubtasksEpic(epic1));
        System.out.println(taskManager.getByIdTask(task.getIdTask()));

        System.out.println("Посмотрим на подзадачу " + subtask);
        System.out.println("Посмотрим на подзадачу " + subtask1);
        System.out.println("Посмотрим на подзадачу " + subtask2);
        System.out.println("Посмотри на статус эпика " + epic);
        System.out.println("Посмотри на статус эпика " + epic1);
        System.out.println("Посмотрим на статус эпика " + epic2);

        Task newTask = new Task("Воскресенье", task.getIdTask(), "день уборки");
        taskManager.updateTask(newTask);
        System.out.println("Посмотри на обновленную задачу " + newTask);

        Epic newEpic = taskManager.getByIdEpic(epic.getIdTask());
        newEpic.setName("Измененный эпик");
        newEpic.setDescription("Изиененное описание эпика");
        taskManager.updateEpic(newEpic);
        System.out.println(newEpic);

        Subtask newSubtask = taskManager.getByIdSubtask(subtask2.getIdTask());
        newSubtask.setName("Измененная подзадача");
        newSubtask.setDescription("Измененное описание подзадачи");
        newSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(newSubtask);
        System.out.println(newSubtask);

        taskManager.deleteByIdTask(task.getIdTask());
        System.out.println(taskManager.getAllTasks());

        taskManager.deleteByIdEpic(epic1.getIdTask());
        System.out.println(taskManager.getAllEpics());

        taskManager.deleteByIdSubtask(subtask2.getIdTask());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

    }

}
