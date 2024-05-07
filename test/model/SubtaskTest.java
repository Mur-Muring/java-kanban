package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    // проверяет, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void comparisonOfSubTasksWithTheSameId() {
        Subtask subtask1 = new Subtask("кот", "гладить", Status.IN_PROGRESS, 1);
        Subtask subtask2 = new Subtask("кот", "гладить", Status.IN_PROGRESS, 1);
        subtask1.setIdTask(2);
        subtask2.setIdTask(2);
        assertEquals(subtask1, subtask2, "Задачи не совпадают.");
    }
}