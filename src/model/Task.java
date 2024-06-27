package model;
//1. Добавила новые поля

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private Integer id;
    private String description;
    private Status status;
    //продолжительность задачи в минутах
    private Duration duration;
    // дата и время, когда предполагается приступить к выполнению задачи
    LocalDateTime startTime;


    public Task(String name, Integer id, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, Integer id, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Task task) {
        this(task.name, task.description, task.startTime, task.duration);
        this.status = task.status;
        this.id = task.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdTask() {
        return id;
    }

    public void setIdTask(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TypeOfTask getTypeOfTask() {
        return TypeOfTask.TASK;
    }

    public Integer getIdEpic() {
        return null;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(id, task.id) && Objects.equals(description, task.description) && status == task.status && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, description, status, duration, startTime);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Task{");
        sb.append("name='").append(name).append('\'');
        sb.append(", id=").append(id);
        sb.append(", description='").append(description).append('\'');
        sb.append(", status=").append(status);
        sb.append(", duration=").append(duration);
        sb.append(", startTime=").append(startTime);
        sb.append('}');
        return sb.toString();
    }
}