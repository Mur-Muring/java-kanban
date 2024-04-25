package model;

import java.util.ArrayList;

public class Epic extends Task { // класс с эпиками
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }
        boolean allNEW = true;
        boolean allDONE = true;
        for (Subtask subtask : this.subtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNEW = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDONE = false;
            }
            if (!allNEW && !allDONE) {
                setStatus(Status.IN_PROGRESS);
            }
        }
        if (allNEW) {
            setStatus(Status.NEW);
        } else if (allDONE) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "ID=" + getIdTask() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getStatus() +
                '}';
    }
}