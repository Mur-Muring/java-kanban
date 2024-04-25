package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {// Менеджер
    // за форматирование извиняюсь, хотела вконце и забыла

    private Integer idCounter = 0;

    // Хранение всех типов данных
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();


    // Создание
    public Task addTask(Task task) {
        task.setIdTask(getIdCounter());
        tasks.put(task.getIdTask(), task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setIdTask(getIdCounter());
        epics.put(epic.getIdTask(), epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getIdEpic());
        if (epic == null) {
            return null;
        }
        subtask.setIdTask(getIdCounter());
        subtasks.put(subtask.getIdTask(), subtask);
        epic.addSubtask(subtask);
        epic.updateStatus();
        return subtask;
    }
    // Обновление
    public void updateTask(Task task){
        if(tasks.get(task.getIdTask())!=null){
            tasks.put(task.getIdTask(), task);
        }
    }

    public void updateEpic(Epic epic) {
        Epic updateEpic = epics.get(epic.getIdTask());
        if (updateEpic == null) {
            return;
        }
        updateEpic.setName(epic.getName());
        updateEpic.setDescription(epic.getDescription());
    }

    // заменила == на "equals" везде, где сравнивала
    public void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getIdTask()) == null) {
            return;
        }
        Epic epic = epics.get(subtask.getIdEpic());
        if (epic == null) {
            return;
        }
        if(subtask.getIdEpic().equals(subtasks.get(subtask.getIdTask()).getIdEpic())){
            ArrayList<Subtask> subtaskForEpic = epic.getSubtasks();
            for (int i = 0; i < subtaskForEpic.size(); i++) {
                if (subtaskForEpic.get(i).getIdTask().equals(subtask.getIdEpic())) {
                    subtaskForEpic.set(i, subtask);
                    epic.addSubtask(subtask);
                    epic.updateStatus();
                    break;
                }
            }
            subtasks.put(subtask.getIdTask(), subtask);
        }
    }

    // Получение списка всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtasksEpic(Epic epic) {
        return new ArrayList<>(epic.getSubtasks());
    }

    // Получение по ID
    public Task getByIdTask(Integer id) {
        return tasks.get(id);
    }

    public Epic getByIdEpic(Integer id) {
        return epics.get(id);
    }

    public Subtask getByIdSubtask(Integer id) {
        return subtasks.get(id);
    }

    // Удаление всего
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            ArrayList<Subtask> epicsSubtaks = epic.getSubtasks();
            epicsSubtaks.clear();
            epic.updateStatus();
        }
    }

    //Удаление по ID

    public void deleteByIdTask(Integer id) {
        tasks.remove(id);
    }

    public void deleteByIdEpic(Integer id) {
        Epic epic = epics.get(id);
        if(epic != null){
            ArrayList<Subtask> epicsSubtasks = epic.getSubtasks();
            for (Subtask subtask : epicsSubtasks) {
                subtasks.remove(subtask.getIdTask());
            } // убрала чистку подзадач
            epics.remove(id);
        }
    }

    public void deleteByIdSubtask(Integer id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getIdEpic());
        if (epic == null) {
            return;
        }
        ArrayList<Subtask> epicsSubtasks = epic.getSubtasks();
        for (int i = 0; i < epicsSubtasks.size(); i++) {
            if (epicsSubtasks.get(i).getIdTask().equals(id)) {
                epicsSubtasks.remove(i); // конечно тут i
                break;
            }
        }
        epic.updateStatus();
    }

    // ну вот он теперь тут одинокий, таких тонкостей у нас не было, спасибо учту
    private int getIdCounter() {
        return idCounter++;
    }

}