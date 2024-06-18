package manager;
// 1. убрала toEnum
// 2. Добавила в Task и Epic классы зашлушки и в результате получилас такой toString

import model.*;

public class Utils {
    static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%d", task.getIdTask(), task.getTypeOfTask(), task.getName(),
                task.getStatus(), task.getDescription(), task.getIdEpic());
    }

    static Task fromString(String value) {
        String[] strings = value.split(",");
        int id = Integer.parseInt(strings[0]);
        TypeOfTask typeOfTask = TypeOfTask.valueOf(strings[1]);
        String name = strings[2];
        Status status = Status.valueOf(strings[3]);
        String description = strings[4];

        switch (typeOfTask) {
            case TASK -> {
                Task task = new Task(name, description, status);
                task.setIdTask(id);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setIdTask(id);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int idEpic = Integer.parseInt(strings[5]);
                Subtask subtask = new Subtask(name, description, status, idEpic);
                subtask.setIdTask(id);
                return subtask;
            }
            default -> throw new IllegalStateException("Неверное значение: " + typeOfTask);
        }
    }
}
