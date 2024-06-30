package manager;
/*
1. Обновила методы, проверила как востанваливает из файла с учетом времени
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("test.csv"));
    }

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
            FileBackedTaskManager fileManagerSave = new FileBackedTaskManager(file);

            Task task1 = new Task("Задача 1", "Описание 1",
                    LocalDateTime.of(2004, 4, 13, 11, 50), Duration.ofMinutes(2));
            fileManagerSave.addTask(task1);
            Task task2 = new Task("Задача 2", "Описание 2",
                    LocalDateTime.of(2024, 4, 13, 13, 50), Duration.ofMinutes(2));
            fileManagerSave.addTask(task2);
            Epic epic1 = new Epic("Эпик1", "Описание 1");
            fileManagerSave.addEpic(epic1);
            Subtask subtask1 = new Subtask("Подзадача 1", "...", Status.NEW, epic1.getIdTask(),
                    LocalDateTime.of(2024, 4, 13, 14, 40), Duration.ofMinutes(2));
            Subtask subtask2 = new Subtask("Подзадача 2", "...", Status.NEW, epic1.getIdTask(),
                    LocalDateTime.of(2025, 4, 13, 11, 50), Duration.ofMinutes(2));
            fileManagerSave.addSubtask(subtask1);
            fileManagerSave.addSubtask(subtask2);
            List<Task> before = fileManagerSave.getPrioritizedTasks();

            FileBackedTaskManager fileManagerLoad = FileBackedTaskManager.loadFromFile(file);
            List<Task> after = fileManagerSave.getPrioritizedTasks();

            Assertions.assertEquals(fileManagerSave.getAllTasks(), fileManagerLoad.getAllTasks(),
                    "Ошибка востановления задач");
            Assertions.assertEquals(fileManagerSave.getAllEpics(), fileManagerLoad.getAllEpics(),
                    "Ошибка востановления эпиков");
            Assertions.assertEquals(fileManagerSave.getAllSubtasks(), fileManagerLoad.getAllSubtasks(),
                    "Ошибка востановления подзадач");
            Assertions.assertEquals(before, after, "После восстановления задачи (подзадачи) не попали в отсортированный список");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}