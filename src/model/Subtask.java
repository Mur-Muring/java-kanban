package model;

import java.time.Duration;
import java.time.LocalDateTime;

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

}
