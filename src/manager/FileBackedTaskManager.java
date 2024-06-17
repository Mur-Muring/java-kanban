package manager;
/*
1. название статик переменной большими буквами (строка 19)
2. Убрала save() из конструктора
3. Переименнова переменные в методах add..()
4. Удалила методы getAll...().
5. Методы в строку/из строки перенесла в утилитарный класс toEnum
6. В методе loadFromFile() заменила использование addTask на добавление в мапу
7. Поменяла текст сообщения собственного исключения
8. save() теперь приватный и переместила его вниз после публичных
 */

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String TITLE = "id,type,name,status,description,epic";

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


            for (String taskString : lines) {
                if (taskString.equals(TITLE) || taskString.isBlank()) {
                    continue;
                }
                Task task = Utils.fromString(taskString);

                switch (task.getTypeOfTask()) {
                    case TASK -> fileBackedTaskManager.tasks.put(task.getIdTask(), task);
                    case EPIC -> fileBackedTaskManager.epics.put(task.getIdTask(), (Epic) task);
                    case SUBTASK -> {
                        fileBackedTaskManager.subtasks.put(task.getIdTask(), (Subtask) task);
                        Subtask subTask = (Subtask) task;
                        Epic epic = fileBackedTaskManager.epics.get(subTask.getIdEpic());
                        epic.addSubTask(subTask);
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
        Epic epic2 = new Epic("Эпик2", "Описание 2");
        fileManager1.addEpic(epic2);
        Task task3 = new Task("Задача 3", "Описание 3");
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
