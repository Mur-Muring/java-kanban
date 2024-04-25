package model;

public class Subtask extends Task {// класс с подзадачами

    private Integer idEpic;

    public Subtask(String name, Integer id, String description, Status status, Integer idEpic) {
        super(name, id, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(String name, String description, Status status, Integer idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }


    public Integer getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(Integer idEpic) {
        this.idEpic = idEpic;
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
