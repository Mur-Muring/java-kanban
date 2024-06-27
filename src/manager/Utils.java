package manager;
// 1. Переписала методы с учетом появления времени
//

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;


import static manager.FileBackedTaskManager.DATE_TIME_FORMATTER;

public class Utils {
   //сохранения задачи в строку
    static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%d,%s", task.getIdTask(), task.getTypeOfTask(), task.getName(),
                task.getStatus(), task.getDescription(), task.getIdEpic(),task.getStartTime() != null ?  task.getStartTime().format(DATE_TIME_FORMATTER): "",
                task.getDuration().toMinutes(),task.getEndTime() != null ? task.getEndTime().format(DATE_TIME_FORMATTER): "");
    }
    //метод создания задачи из строки
    static Task fromString(String value) {
        String[] strings = value.split(",");
        int id = Integer.parseInt(strings[0]);
        TypeOfTask typeOfTask = TypeOfTask.valueOf(strings[1]);
        String name = strings[2];
        Status status = Status.valueOf(strings[3]);
        String description = strings[4];

        LocalDateTime startDataTime=null;
        if (!strings[6].isBlank()) {
            startDataTime=LocalDateTime.parse(strings[6], DATE_TIME_FORMATTER);
        }
        Duration duration = Duration.ofMinutes(Integer.parseInt(strings[7]));

        LocalDateTime endDataTime=null;
        if (!strings[8].isBlank()) {
           endDataTime=LocalDateTime.parse(strings[8], DATE_TIME_FORMATTER);
        }

        switch (typeOfTask) {
            case TASK -> {
                Task task = new Task(name, description, status,startDataTime,duration);
                task.setIdTask(id);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setIdTask(id);
                epic.setStatus(status);
                epic.setStartTime(startDataTime);
                epic.setDuration(duration);
                epic.setEndTime(endDataTime);
                return epic;
            }
            case SUBTASK -> {
                int idEpic = Integer.parseInt(strings[5]);
                Subtask subtask = new Subtask(name, description, status, idEpic,startDataTime,duration);
                subtask.setIdTask(id);
                return subtask;
            }
            default -> throw new IllegalStateException("Неверное значение: " + typeOfTask);
        }
    }
}
