package manager;
/*
1. после восстановления задачи попадают в остортированный список
 */

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static manager.Utils.fromString;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String TITLE = "id,type,name,status,description,epic, start_time, duration, end_time";
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task addTask(Task task) {
        Task taskSave = super.addTask(task);
        save();
        return taskSave;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic epicSave = super.addEpic(epic);
        save();
        return epicSave;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask subtaskSave = super.addSubtask(subtask);
        save();
        return subtaskSave;
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

    public static FileBackedTaskManager loadFromFile(File file) {
        try {

            String[] lines = Files.readString(file.toPath()).split("\n");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            List<Task> tasksList = Arrays.stream(lines)
                    .skip(1)
                    .map(line -> Optional.of(fromString(line)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            for (Task task : tasksList) {
                switch (task.getTypeOfTask()) {
                    case TASK -> {
                        fileBackedTaskManager.tasks.put(task.getIdTask(), task);
                        fileBackedTaskManager.prioritizedTasks.add(task);
                    }
                    case EPIC -> fileBackedTaskManager.epics.put(task.getIdTask(), (Epic) task);
                    case SUBTASK -> {
                        fileBackedTaskManager.subtasks.put(task.getIdTask(), (Subtask) task);
                        Subtask subTask = (Subtask) task;
                        Epic epic = fileBackedTaskManager.epics.get(subTask.getIdEpic());
                        epic.addSubTask(subTask);
                        fileBackedTaskManager.prioritizedTasks.add(task);
                    }
                    default -> throw new IllegalStateException("Неверное значение");
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка восстановления из файла");
        }
    }


    private void save() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(file.toURI()), StandardCharsets.UTF_8)) {
            bufferedWriter.write(TITLE);

            for (Task task : tasks.values()) {
                String newString = Utils.toString(task);
                bufferedWriter.write(String.format("\n%s", newString));
            }
            for (Epic epic : epics.values()) {
                String newString = Utils.toString(epic);
                bufferedWriter.write(String.format("\n%s", newString));
            }
            for (Subtask subtask : subtasks.values()) {
                String newString = Utils.toString(subtask);
                bufferedWriter.write(String.format("\n%s", newString));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    public static void main(String[] args) {
        File fileTest = new File("fileTest.csv");

        FileBackedTaskManager fileManager1 = new FileBackedTaskManager(fileTest);

        Task task1 = new Task("Задача 1", "Описание 1", LocalDateTime.now(), Duration.ofMinutes(2));
        fileManager1.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание 2", LocalDateTime.now().plusHours(1), Duration.ofMinutes(20));
        fileManager1.addTask(task2);

        Epic epic1 = new Epic("Эпик1", "Описание 1");
        fileManager1.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "...", Status.NEW, epic1.getIdTask(), LocalDateTime.now().plusHours(2), Duration.ofMinutes(2));
        Subtask subtask2 = new Subtask("Подзадача 2", "...", Status.NEW, epic1.getIdTask(), LocalDateTime.now().plusHours(3), Duration.ofMinutes(20));
        fileManager1.addSubtask(subtask1);
        fileManager1.addSubtask(subtask2);
        Epic epic2 = new Epic("Эпик2", "Описание 2");
        fileManager1.addEpic(epic2);
        Task task3 = new Task("Задача 3", "Описание 3", LocalDateTime.now().plusHours(4), Duration.ofMinutes(20));
        fileManager1.addTask(task3);

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
