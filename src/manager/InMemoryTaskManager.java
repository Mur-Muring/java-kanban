package manager;
/*
1. Написала метод расчета временных полей эпика. Хотела сделать так изнасально, но мне казалось, что работать с
конкретными значениями быстрее, чем бегать по всем спискам
 */

import exception.NotFoundException;
import exception.TimeConflictException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    private Integer idCounter = 0;

    HistoryManager historyManager = Managers.getDefaultHistory();

    // Хранение всех типов данных
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    // Создание
    @Override
    public Task addTask(Task task) {
        if (!isValidTaskForTime(task)) {
            throw new IllegalArgumentException("Пересмотрите временные параметры");
        }
        if (timeConflict(task)) {
            throw new TimeConflictException("Задачи пересекаются во времени");
        }
        if (task != null) {
            task.setIdTask(getIdCounter());
            tasks.put(task.getIdTask(), task);
            updatePrioritizedTasks();
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
        if (!isValidTaskForTime(subtask)) {
            throw new IllegalArgumentException("Пересмотрите временные параметры");
        }
        if (timeConflict(subtask)) {
            throw new TimeConflictException("Задачи пересекаются во времени");
        }
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
        updateTimeForEpic(epicId);
        updatePrioritizedTasks();

        return subtask;
    }

    // Обновление
    @Override
    public void updateTask(Task task) {
        if (!isValidTaskForTime(task)) {
            throw new IllegalArgumentException("Пересмотрите временные параметры");
        }
        if (tasks.get(task.getIdTask()) != null) {
            tasks.put(task.getIdTask(), task);
            updatePrioritizedTasks();
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
        if (!isValidTaskForTime(subtask)) {
            throw new IllegalArgumentException("Пересмотрите временные параметры");
        }
        if (epics.containsKey(subtask.getIdEpic())) {
            subtasks.put(subtask.getIdTask(), subtask);
            int epicId = subtasks.get(subtask.getIdTask()).getIdEpic();
            updateStatus(epicId);
            updatePrioritizedTasks();
            updateTimeForEpic(subtask.getIdEpic());
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
    public List<Subtask> getSubtasksEpic(Integer id) {
        return epics.get(id).getSubtasks().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
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
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
        updatePrioritizedTasks();
    }

    @Override
    public void deleteEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.clear();
        updatePrioritizedTasks();
    }

    @Override
    public void deleteSubtasks() {
        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            updateStatus(epic.getIdTask());
            updateTimeForEpic(epic.getIdTask());
        });
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        updatePrioritizedTasks();
    }

    //Удаление по ID
    @Override
    public void deleteByIdTask(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
        updatePrioritizedTasks();
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
        updateTimeForEpic(epicId);
        updatePrioritizedTasks();
    }

    // История
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int getIdCounter() {
        return idCounter++;
    }

    //Приоритетность задач
    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private void updatePrioritizedTasks() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(tasks.values().stream()
                .filter(task -> task.getStartTime().toLocalDate() != null)
                .toList());

        prioritizedTasks.addAll(subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime().toLocalDate() != null)
                .toList());
    }

    // Метод валидации
    private boolean isValidTaskForTime(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }

    // конфликт по времени задач
    private boolean timeConflict(Task task) {
        List<Task> sortTask = getPrioritizedTasks();
        return sortTask.stream()
                .anyMatch(existingTask -> !existingTask.getEndTime().isBefore(task.getStartTime()) &&
                        !task.getEndTime().isBefore(existingTask.getStartTime()));
    }

    private void updateTimeForEpic(int idEpic) {
        Epic epic = epics.get(idEpic);

        if (epic != null) {
            List<Subtask> subtaskList = getSubtasksEpic(idEpic);
            LocalDateTime startTime = subtaskList.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime endTime = subtaskList.stream()
                    .map(subtaskObj -> subtaskObj.getStartTime().plus(subtaskObj.getDuration()))
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            epic.setStartTime(startTime);
            epic.setEndTime(endTime);

            Duration duration = subtaskList.stream()
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);

            epic.setDuration(duration);
        }
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