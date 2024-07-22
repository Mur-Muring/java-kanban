package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, Duration.ofMinutes(0));
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubTask(Subtask subtask) {
        int id = subtask.getIdTask();
        this.subtasks.add(id);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public TypeOfTask getTypeOfTask() {
        return TypeOfTask.EPIC;
    }

    public Integer getIdEpic() {
        return null;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
