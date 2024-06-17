package manager;
// 1. в методе toEnum заменила instanceof на геттеры их классов задач
// 2. Убрала лишний метод определения статуса

import model.*;

public class Utils {
    static String toString(Task task) {
        TypeOfTask typeOfTask = toEnum(task);
        switch (typeOfTask) {
            case TASK, EPIC -> {
                return String.format("%d,%s,%s,%s,%s,", task.getIdTask(), typeOfTask, task.getName(), task.getStatus(),
                        task.getDescription());
            }
            case SUBTASK -> {
                Subtask subTask = (Subtask) task;
                return String.format("%d,%s,%s,%s,%s,%d", subTask.getIdTask(), typeOfTask, subTask.getName(),
                        subTask.getStatus(), subTask.getDescription(), subTask.getIdEpic());
            }
        }
        throw new IllegalStateException("Неверный тип задач: " + typeOfTask);
    }

    static TypeOfTask toEnum(Task task) {
        if (task.getTypeOfTask().equals(TypeOfTask.EPIC)) {
            return TypeOfTask.EPIC;
        } else if (task.getTypeOfTask().equals(TypeOfTask.SUBTASK)) {
            return TypeOfTask.SUBTASK;
        }
        return TypeOfTask.TASK;
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
