package manager;
/*
Добавила тест на сохранение и восстановления разных типов задач из файла
 */

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest {
    // Сохранение пустого файла
    @Test
    void savingAnEmptyFileTest() {
        try {
            File file = File.createTempFile("test", "csv");
            FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

            String[] lines = Files.readString(file.toPath()).split("\n");
            Assertions.assertEquals(lines.length, 1, "Ошибка загрузки пустого файла");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Загрузка из пустого файла
    @Test
    void loadingAnEmptyFileTest() {
        try {
            File file = File.createTempFile("test", "csv");
            FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

            Assertions.assertEquals(fileManager.getAllTasks().size(), 0);
            Assertions.assertEquals(fileManager.getAllEpics().size(), 0);
            Assertions.assertEquals(fileManager.getAllSubtasks().size(), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //сохранение и воствновление нескольких задач
    @Test
    void savingTasksTest() {
        try {
            File file = File.createTempFile("test", "csv");
            FileBackedTaskManager fileManager1 = new FileBackedTaskManager(file);

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

            FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(file);

            Assertions.assertEquals(fileManager1.getAllTasks(), fileManager2.getAllTasks(),
                    "Ошибка востановления задач");
            Assertions.assertEquals(fileManager1.getAllEpics(), fileManager2.getAllEpics(),
                    "Ошибка востановления эпиков");
            Assertions.assertEquals(fileManager2.getAllSubtasks(), fileManager2.getAllSubtasks(),
                    "Ошибка востановления подзадач");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}