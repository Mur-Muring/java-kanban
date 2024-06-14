package manager;

import exception.ManagerSaveException;
import model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String title = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
        save();
    }

    @Override
    public Task addTask(Task task) {
        Task task1 = super.addTask(task);
        save();
        return task1;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic epic1 = super.addEpic(epic);
        save();
        return epic1;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask subtask1 = super.addSubtask(subtask);
        save();
        return subtask1;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteByIdTask(Integer id) {
        super.deleteByIdTask(id);
        save();
    }

    @Override
    public void deleteByIdEpic(Integer id) {
        super.deleteByIdEpic(id);
        save();
    }

    @Override
    public void deleteByIdSubtask(Integer id) {
        super.deleteByIdSubtask(id);
        save();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        save();
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        save();
        return super.getAllEpics();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        save();
        return super.getAllSubtasks();
    }

    public void save() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(file.toURI()), StandardCharsets.UTF_8)) {
            bufferedWriter.write(title);

            for (Task task : tasks.values()) {
                String newString = toString(task);
                bufferedWriter.write(String.format("\n%s", newString));
            }
            for (Epic epic : epics.values()) {
                String newString = toString(epic);
                bufferedWriter.write(String.format("\n%s", newString));
            }
            for (Subtask subtask : subtasks.values()) {
                String newString = toString(subtask);
                bufferedWriter.write(String.format("\n%s", newString));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {

            String[] lines = Files.readString(file.toPath()).split("\n");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);


            for (String taskString : lines) {
                if (taskString.equals(title) || taskString.isBlank()) {
                    continue;
                }
                Task task = fromString(taskString);
                TypeOfTask typeOfTask = toEnum(task);

                switch (typeOfTask) {
                    case TASK -> fileBackedTaskManager.addTask(task);
                    case EPIC -> fileBackedTaskManager.addEpic((Epic) task);
                    case SUBTASK -> fileBackedTaskManager.addSubtask((Subtask) task);
                    default -> throw new IllegalStateException("Неверное значение: " + typeOfTask);
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private static String toString(Task task) {
        TypeOfTask typeOfTask = toEnum(task);
        switch (typeOfTask) {
            case TASK, EPIC -> {
                return String.format("%d,%s,%s,%s,%s,", task.getIdTask(), typeOfTask, task.getName(), task.getStatus(),
                        task.getDescription());
            }
            case SUBTASK -> {
                Subtask subTask = (Subtask) task;
                return String.format("%d,%s,%s,%s,%s,%d", subTask.getIdTask(), typeOfTask, subTask.getName(),
                        subTask.getStatus(), subTask.getDescription(), subTask.getIdEpic());
            }
        }

        throw new IllegalStateException("Неверный тип задач: " + typeOfTask);
    }

    private static TypeOfTask toEnum(Task task) {
        if (task instanceof Epic) {
            return TypeOfTask.EPIC;
        } else if (task instanceof Subtask) {
            return TypeOfTask.SUBTASK;
        }
        return TypeOfTask.TASK;
    }

    private static Task fromString(String value) {
        String[] strings = value.split(",");
        int id = Integer.parseInt(strings[0]);
        TypeOfTask typeOfTask = TypeOfTask.valueOf(strings[1]);
        String name = strings[2];
        Status status = statusFromString(strings[3]);
        String description = strings[4];

        switch (typeOfTask) {
            case TASK -> {
                Task task = new Task(name, description, status);
                task.setIdTask(id);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setIdTask(id);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int idEpic = Integer.parseInt(strings[5]);
                Subtask subtask = new Subtask(name, description, status, idEpic);
                subtask.setIdTask(id);
                return subtask;
            }
            default -> throw new IllegalStateException("Неверное значение: " + typeOfTask);
        }
    }

    private static Status statusFromString(String string) {
        return switch (string) {
            case "NEW" -> Status.NEW;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> throw new IllegalStateException("Неверное значение: " + string);
        };
    }

    public static void main(String[] args) {
        File fileTest = new File("fileTest.csv");

        FileBackedTaskManager fileManager1 = new FileBackedTaskManager(fileTest);

        Task task1 = new Task("Задача 1", "Описание 1");
        fileManager1.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание 2");
        fileManager1.addTask(task2);

        Epic epic1 = new Epic("Эпик1", "Описание 1");
        fileManager1.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "...", Status.NEW, epic1.getIdTask());
        Subtask subtask2 = new Subtask("Подзадача 2", "...", Status.NEW, epic1.getIdTask());
        fileManager1.addSubtask(subtask1);
        fileManager1.addSubtask(subtask2);

        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(fileTest);

        if (fileManager1.getAllTasks().size() != fileManager2.getAllTasks().size()) {
            System.out.println("Количество задач не совпадает");
        }
        if (fileManager1.getAllEpics().size() != fileManager2.getAllEpics().size()) {
            System.out.println("Количество эпиков не совпадает");
        }
        if (fileManager1.getAllSubtasks().size() != fileManager2.getAllSubtasks().size()) {
            System.out.println("Количество эпиков не совпадает");
        }
    }
}
