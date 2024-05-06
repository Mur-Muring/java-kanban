package Test.Model;


import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    // проверяет, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void comparisonOfTasksWithTheSameId() {
        Task task1 = new Task("Уборка", "Убрать комнату");
        Task task2 = new Task("Уборка", "Убрать комнату");
        task1.setIdTask(1);
        task2.setIdTask(1);
        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}