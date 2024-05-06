package Test.Model;


import model.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    // проверяет, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void comparisonOfEpicsWithTheSameId() {
        Epic epic1 = new Epic("собака", "прогулка");
        Epic epic2 = new Epic("собака", "прогулка");
        epic1.setIdTask(3);
        epic2.setIdTask(3);
        assertEquals(epic1, epic2, "Задачи не совпадают.");
    }
}