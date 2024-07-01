package model;


import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    // проверяет, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void comparisonOfTasksWithTheSameId() {
        Task task1 = new Task("Уборка", "Убрать комнату",
                LocalDateTime.of(1994, 4, 13, 11, 50), Duration.ofMinutes(2));
        Task task2 = new Task("Уборка", "Убрать комнату",
                LocalDateTime.of(1994,4,13,11,50), Duration.ofMinutes(2));
        task1.setIdTask(1);
        task2.setIdTask(1);
        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}