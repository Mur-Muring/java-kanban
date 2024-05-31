
package manager;
/*
1.Добавила во всех методах, связвнных с удалением по id - удалении из истории, реализовала Main, там все работало,
но сломало мне тесты по удалению по id
 */

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private Integer idCounter = 0;

    HistoryManager historyManager = Managers.getDefaultHistory();

    // Хранение всех типов данных
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    // Создание
    @Override
    public Task addTask(Task task) {
        if (task != null) {
            task.setIdTask(getIdCounter());
            tasks.put(task.getIdTask(), task);
        }
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setIdTask(getIdCounter());
        epics.put(epic.getIdTask(), epic);
        return epic;
    }

    @Override
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
    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getIdTask()) != null) {
            tasks.put(task.getIdTask(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic updateEpic = epics.get(epic.getIdTask());
        if (updateEpic == null) {
            return;
        }
        updateEpic.setName(epic.getName());
        updateEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getIdEpic())) {
            subtasks.put(subtask.getIdTask(), subtask);
            int epicId = subtasks.get(subtask.getIdTask()).getIdEpic();
            updateStatus(epicId);
        }
    }

    // Получение списка всех задач
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksEpic(Integer id) {
        ArrayList<Integer> subtasksID = epics.get(id).getSubtasks();
        ArrayList<Subtask> subtasksEpic = new ArrayList<>();
        for (int subtaskId : subtasksID) {
            subtasksEpic.add(subtasks.get(subtaskId));
        }
        return subtasksEpic;
    }

    // Получение по ID
    @Override
    public Task getByIdTask(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getByIdEpic(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getByIdSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // Удаление всего
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateStatus(epic.getIdTask());
        }
        subtasks.clear();
    }

    //Удаление по ID
    @Override
    public void deleteByIdTask(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteByIdEpic(Integer id) {
        ArrayList<Integer> subtaskIds = epics.get(id).getSubtasks();
        for (Integer subtaskId : subtaskIds) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteByIdSubtask(Integer id) {
        int epicId = subtasks.get(id).getIdEpic();
        ArrayList<Integer> subtaskIds = epics.get(epicId).getSubtasks();
        subtaskIds.remove(id);
        subtasks.remove(id);
        updateStatus(epicId);
        historyManager.remove(id);
    }

    // История
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int getIdCounter() {
        return idCounter++;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

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