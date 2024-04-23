import java.util.ArrayList;
public class Main {
        public static void main(String[] args) {
                TaskManager taskManager = new TaskManager();
                Task task = new Task("Понедельник", "Описание понедельника", Status.NEW);
                taskManager.addTask(task);
                Task task1 = new Task("Вторник", "Описание вторника", Status.NEW);
                taskManager.addTask(task1);
                Task task2 = new Task("Среда", "Описание среды", Status.NEW);
                taskManager.addTask(task2);

                System.out.println("Что же мы видим тут " + task);
                System.out.println("А вот тут " + task1);
                System.out.println("И вот тут " + task2);

                Epic epic = new Epic("Эпическая задача Понедельник", "Описание эпика Пн");
                Epic epic1 = new Epic("Эпическая задача Вторник", "Описание эпика Вт");
                Epic epic2 = new Epic("Эпическая задача Среда", "Описание эпика Вт");

                taskManager.addEpic(epic);
                taskManager.addEpic(epic1);
                taskManager.addEpic(epic2);

                System.out.println("Что же мы видим тут " + epic);
                System.out.println("А вот тут " + epic1);
                System.out.println("И вот тут " + epic2);

                Subtask subtask = new Subtask("утро", "постараться встать", Status.IN_PROGRESS,
                        epic.getIdTask());
                Subtask subtask1 = new Subtask("день", "постараться выжить", Status.NEW,
                        epic.getIdTask());
                Subtask subtask2 = new Subtask("вечер", "можно и отдохнуть", Status.DONE,
                        epic.getIdTask());

                taskManager.addSubtask(subtask);
                taskManager.addSubtask(subtask1);
                taskManager.addSubtask(subtask2);

                taskManager.getSubtasksEpic(epic2);
                taskManager.getSubtasksEpic(epic2);
                taskManager.getSubtasksEpic(epic2);
                System.out.println(taskManager.getSubtasksEpic(epic2));
                System.out.println(taskManager.getByIdTask(task.getIdTask()));

                System.out.println("Что же мы видим тут " + subtask);
                System.out.println("А вот тут " + subtask1);
                System.out.println("И вот тут " + subtask2);
                System.out.println("А что там со статусом " + epic2);

                Task newTask = new Task("Четверг", task2.getIdTask(), "Четверг - это маленькая пятница",
                        Status.NEW);
                taskManager.updateTask(newTask);
                System.out.println("А что у нас с четвергом " + newTask);

                Epic newEpic = taskManager.getByIdEpic(epic1.getIdTask());
               newEpic.setName("Пятница");
                newEpic.setDescription("Видели ночь, гуляли всю ночь до утра");
                taskManager.updateEpic(newEpic);
                System.out.println(newEpic);

                Subtask newSubtask = taskManager.getByIdSubtask(subtask.getIdTask());
                newSubtask.setName("Зарядка");
                newSubtask.setDescription("Можно не делать");
                newSubtask.setStatus(Status.IN_PROGRESS);
                taskManager.updateSubtask(newSubtask);
                System.out.println(newSubtask);

                taskManager.deleteByIdTask(task1.getIdTask());
                System.out.println(taskManager.getAllTasks());

                taskManager.deleteByIdEpic(epic1.getIdTask());
                System.out.println(taskManager.getAllEpics());

                taskManager.deleteByIdSubtask(subtask1.getIdTask());
                System.out.println(taskManager.getAllSubtasks());

                taskManager.deleteTasks();
                taskManager.deleteEpics();
                taskManager.deleteSubtasks();
                System.out.println(taskManager.getAllTasks());
                System.out.println(taskManager.getAllEpics());
                System.out.println(taskManager.getAllSubtasks());






        }
}
