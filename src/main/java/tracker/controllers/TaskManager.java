package tracker.controllers;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<SubTask> getAllSubTasks();

    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubTasks();

    Task getTaskByID(Long id); // используется для корректной работы кода, на history не влияют
    Epic getEpicByID(Long id); // используется для корректной работы кода, на history не влияют
    SubTask getSubTaskByID(Long id); // используется для корректной работы кода, на history не влияют

    Task getTaskUser(Long id); // используется пользователем, именно эти запросы сохраняются в history
    Epic getEpicUser(Long id); // используется пользователем, именно эти запросы сохраняются в history
    SubTask getSubTaskUser(Long id); // используется пользователем, именно эти запросы сохраняются в history

    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubTask(SubTask subTask);

    void deleteTaskByID(Long id);
    void deleteEpicByID(Long id);
    void deleteSubTaskByID(Long id);

    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubTask(SubTask subTask);

    List<SubTask> getSubTasksByEpicId(Long id);

    List<Task> getHistory();

    LocalDateTime calcStartTimeEpic (Long id);
    LocalDateTime calcEndTimeEpic (Long id);
    Long calcDurationEpic (Long id);

    Set<Task> getPrioritizedTasks();
    boolean isDateTimeFree(Task task);

}
