package manager;
/*
1. При добавлении задач и подзадач учитывает время и продолжительность
2. Пересчитваает эти параметры для эпика в зависимости от подзадач при добвалении подзадач или удалении
3. Создала множество уникальных задач отсортированных по времени, задачу нельзя добавить, если отсуствует дата
4. Добавила методы валидации и конфликта времени, интегрировала их в методы добавления и обновления, в случае ошибки
вызывается собственое исключение
5. Меняла циклф на Stream API
 */

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

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    // Создание
    @Override
    public Task addTask(Task task) throws TimeConflictException {
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
    public Subtask addSubtask(Subtask subtask) throws TimeConflictException {
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
        if (epic.getStartTime() == null || epic.getStartTime().isAfter(subtask.getStartTime())) {
            epic.setStartTime(subtask.getStartTime());
        }
        LocalDateTime endTimeSubtask = subtask.getEndTime();
        if (epic.getEndTime() == null || epic.getEndTime().isBefore(endTimeSubtask)) {
            epic.setEndTime(endTimeSubtask);
        }
        epic.setDuration(epic.getDuration().plus(subtask.getDuration()));

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
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
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

        Epic epic = epics.get(epicId);
        Duration durationSubtask = subtasks.get(id).getDuration();
        LocalDateTime startSubtask = subtasks.get(id).getStartTime();
        LocalDateTime endSubtask = subtasks.get(id).getEndTime();
        epic.setDuration(epic.getDuration().minus(durationSubtask));

        subtaskIds.remove(id);
        subtasks.remove(id);
        updateStatus(epicId);
        historyManager.remove(id);

        List<Subtask> subtaskList = getSubtasksEpic(epicId);
        Comparator<Subtask> comparator = Comparator.comparing(Subtask::getStartTime);

        if (epic.getStartTime().equals(startSubtask)) {
            Optional<Subtask> minTimeSubtask = subtaskList.stream().min(comparator);
            minTimeSubtask.ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));
        }
        if (epic.getEndTime().equals(endSubtask)) {
            Optional<Subtask> maxTimeSubtask = subtaskList.stream().max(comparator);
            maxTimeSubtask.ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));
        }
        updatePrioritizedTasks();
    }

    // История
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
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
    public boolean timeConflict(Task task) {
        List<Task> sortTask = getPrioritizedTasks();
        return sortTask.stream()
                .anyMatch(existingTask -> !existingTask.getEndTime().isBefore(task.getStartTime()) &&
                        !task.getEndTime().isBefore(existingTask.getStartTime()));
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