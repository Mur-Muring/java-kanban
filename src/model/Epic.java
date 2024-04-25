package model;

import java.util.ArrayList;

public class Epic extends Task { // класс с эпиками
  /* решила я рискнуть и переписать на хранение ID,
  надесь не пожалею об этом, так как переписала половину менеджера
   */
    private final ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Integer integer) {
        this.subtasks.add(integer);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }
}