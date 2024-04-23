import java.util.Objects;

public class Task {// класс с задачами
    private String name;
    private Integer id;
    private String description;
    private Status status;

    public Task(String name, Integer id, String description, Status status) {
       setName(name);
       setIdTask(id);
       setDescription(description);
       setStatus(status);
    }

    public Task(String name, String description, Status status) {
        setName(name);
        setDescription(description);
        setStatus(status);
    }

    public Task(String name, String description) {
        setName(name);
        setDescription(description);
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

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null && this.getClass()!=o.getClass()) return false;
        Task task=(Task) o;
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
