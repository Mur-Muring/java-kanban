package model;

public class Subtask extends Task {// класс с подзадачами
    private Integer idEpic;

    public Subtask(String name, String description, Status status, Integer idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "ID=" + getIdTask() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", taskStatus=" + getStatus() +
                '}';
    }
}
