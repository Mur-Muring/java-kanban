package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private final Integer idEpic;

    public Subtask(String name, String description, Status status, Integer idEpic, LocalDateTime localDateTime, Duration duration) {
        super(name, description, status, localDateTime, duration);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    public TypeOfTask getTypeOfTask() {
        return TypeOfTask.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(idEpic, subtask.idEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        return "Subtask{" + "idEpic=" + idEpic +
                '}';
    }
}
