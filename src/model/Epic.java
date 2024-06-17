package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task { // класс с эпиками
    private final ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubTask(Subtask subtask) {
        int id = subtask.getIdTask();
        this.subtasks.add(id);
    }

    public TypeOfTask getTypeOfTask() {
        return TypeOfTask.EPIC;
    }

    public Integer getIdEpic() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }
}