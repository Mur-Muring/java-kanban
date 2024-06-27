package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    // проверяет, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void comparisonOfSubTasksWithTheSameId() {
        Subtask subtask1 = new Subtask("кот", "гладить", Status.IN_PROGRESS, 1,
                LocalDateTime.of(1994, 4, 13, 11, 50), Duration.ofMinutes(2));
        Subtask subtask2 = new Subtask("кот", "гладить", Status.IN_PROGRESS, 1,
                LocalDateTime.of(1994, 4, 13, 11, 50), Duration.ofMinutes(2));
        subtask1.setIdTask(2);
        subtask2.setIdTask(2);
        assertEquals(subtask1, subtask2, "Задачи не совпадают.");
    }
}