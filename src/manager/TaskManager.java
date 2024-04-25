package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {// Менеджер
    // за форматирование извиняюсь, хотела вконце и забыла, надеюсь не забуду в этот раз

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
        int epicId = subtask.getIdEpic();
        ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtasks();
        subtaskIds.add(subtask.getIdTask());
        updateStatus(epicId);
        return subtask;
    }

    // Обновление
    public void updateTask(Task task) {
        if (tasks.get(task.getIdTask()) != null) {
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


    public void updateSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdEpic())) {
            subtasks.put(subtask.getIdTask(), subtask);
            int epicId = subtasks.get(subtask.getIdTask()).getIdEpic();
            updateStatus(epicId);
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
        return new ArrayList<>(subtasks.values());
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
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateStatus(epic.getIdTask());
        }
        subtasks.clear();
    }

    //Удаление по ID

    public void deleteByIdTask(Integer id) {
        tasks.remove(id);
    }

    public void deleteByIdEpic(Integer id) {
        ArrayList<Integer> subtaskIds = epics.get(id).getSubtasks();
        for (Integer subtaskId : subtaskIds) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }


    public void deleteByIdSubtask(Integer id) {
        int epicId = subtasks.get(id).getIdEpic();
        ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtasks();
        subtaskIds.remove(id);
        subtasks.remove(id);
        updateStatus(epicId);
    }


    // ну вот он теперь тут одинокий, таких тонкостей у нас не было, спасибо учту
    private int getIdCounter() {
        return idCounter++;
    }

    // а нет к нему пришла проверка статуса
    private void updateStatus(int idEpic) {
        int counterNew = 0;
        int counterDone = 0;
        ArrayList<Integer> subtaskIds = epics.get(idEpic).getSubtasks();
        for (Integer subtaskId : subtaskIds) {
            if (subtasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                counterNew++;
            } else if (subtasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                counterDone++;
            }
        }
        if (subtaskIds.size() == counterNew || subtaskIds.isEmpty()) {
            epics.get(idEpic).setStatus(Status.NEW);
        } else if (subtaskIds.size() == counterDone) {
            epics.get(idEpic).setStatus(Status.DONE);
        } else {
            epics.get(idEpic).setStatus(Status.IN_PROGRESS);
        }
    }

}