package model;

import java.util.Objects;

public class Task {// класс с задачами
    private String name;
    private Integer id;
    private String description;
    private Status status;

    /* поменяла, теперь все новые задачи по умолчанию будут NEW, а при обновлении если пользователь
    не укажет статус, то NEW либо сможет задать статус сам (условиями задачи не запрешено)
     */

    public Task(String name, Integer id, String description, Status status) {
        this.name = name;
        this.id=id;
        this.description = description;
        this.status=status;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status=status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status=Status.NEW;
    }

    public Task(String name, Integer id, String description) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.status=Status.NEW;
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
    // исправила И на ИЛИ (неправильно обьединила два условия)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}