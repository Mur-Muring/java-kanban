import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
// Менеджер
    private Integer idCounter=0;
    public Integer getIdCounter() {
        return idCounter++;
    }
    // Хранение всех типов данных
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics= new HashMap<>();

    // Создание
    public Task addTask(Task task){
        task.setIdTask(getIdCounter());
        tasks.put(task.getIdTask(), task);
        return task;
    }

    public Epic addEpic(Epic epic){
        epic.setIdTask(getIdCounter());
        epics.put(epic.getIdTask(),epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask){
        Epic epic=epics.get(subtask.getIdEpic());
        if(epic==null){
            return null;
        }
        subtask.setIdTask(getIdCounter());
        subTasks.put(subtask.getIdTask(), subtask);
        epic.updateStatus();
        return subtask;
    }

   // Обновление
    public void updateTask(Task task){
        if(tasks.get(task.getIdTask())!=null){
            tasks.put(task.getIdTask(), task);
        }
    }

    public void updateEpic(Epic epic){
        Epic newEpic = epics.get(epic.getIdTask());
        newEpic.setName(epic.getName());
        newEpic.setDescription(epic.getDescription());
    }

    public void updateSubtask (Subtask subtask){
        Epic epic=epics.get(subtask.getIdEpic());
        if(subtask.getIdEpic()==subTasks.get(subtask.getIdTask()).getIdEpic()){
            ArrayList<Subtask> subtaskEpic = epic.getSubtasks();
            for (int i = 0; i < subtaskEpic.size(); i++) {
                if(subtaskEpic.get(i).getIdTask()==subtask.getIdTask()){
                    subtaskEpic.set(i, subtask);
                    epic.addSubtasks(subtask);
                    epic.updateStatus();
                    break;
                }
            }
            subTasks.put(subtask.getIdTask(),subtask);
        }
    }

    // Получение списка всех задач
    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(tasks.values());
    }
    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
    }
    public ArrayList<Subtask> getAllSubtasks(){
        return new ArrayList<>(subTasks.values());
    }
    public ArrayList<Subtask> getSubtasksEpic(Epic epic){
        return new ArrayList<>(epic.getSubtasks());
    }

    // Получение по ID
    public Task getByIdTask(Integer id){
        return tasks.get(id);
    }
    public Epic getByIdEpic(Integer id){
        return epics.get(id);
    }
    public Subtask getByIdSubtask(Integer id){
        return subTasks.get(id);
    }

    // Удаление всего
    public void deleteTasks(){
        tasks.clear();
    }
    public void deleteEpics(){
        subTasks.clear();
        epics.clear();
    }
    public void deleteSubtasks(){
        subTasks.clear();
        for (Epic epic:epics.values()){
            ArrayList<Subtask>subtasksEpic=epic.getSubtasks();
            subtasksEpic.clear();
            epic.updateStatus();
        }
    }

    //Удаление по ID
    public void deleteByIdTask(Integer id){
        tasks.remove(id);
    }

    public void deleteByIdEpic(Integer id){
        Epic epic = epics.get(id);
        if(epic!=null) {
            ArrayList<Subtask> subtasksEpic=epic.getSubtasks();
            for (Subtask subtask:subtasksEpic){
                subTasks.remove(subtask.getIdTask());
            }
            epic.getSubtasks().clear();
            epics.remove(id);
        }
    }

    public void deleteByIdSubtask(Integer id){
        Subtask subtask=subTasks.remove(id);
        Epic epic=epics.get(subtask.getIdEpic());
        ArrayList<Subtask> subtasksEpic=epic.getSubtasks();
        for (int i = 0; i < subtasksEpic.size(); i++){
            if(subtasksEpic.get(i).getIdTask()==id){
                subtasksEpic.remove(id);
                break;
            }
        }
        epic.updateStatus();
    }


}
