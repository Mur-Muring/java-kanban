package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    // Создание
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    // Обновление
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // Получение списка всех задач
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    // Изменила вывод подзадач конктретного эпика
    List<Subtask> getSubtasksEpic(Integer id);

    // Получение по ID
    Task getByIdTask(Integer id);

    Epic getByIdEpic(Integer id);

    Subtask getByIdSubtask(Integer id);

    // Удаление всего
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    //Удаление по ID
    void deleteByIdTask(Integer id);

    void deleteByIdEpic(Integer id);

    void deleteByIdSubtask(Integer id);

    List<Task> getHistory();

    //Приоритетность задач
    List<Task> getPrioritizedTasks();

}
