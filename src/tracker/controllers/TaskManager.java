package tracker.controllers;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    public List<Task> getAllTasks();
    public List<Epic> getAllEpics();
    public List<SubTask> getAllSubTasks();

    public void deleteAllTasks();
    public void deleteAllEpics();
    public void deleteAllSubTasks();

    public Task getTaskByID(Long id); // используется для корректной работы кода, на history не влияют
    public Epic getEpicByID(Long id); // используется для корректной работы кода, на history не влияют
    public SubTask getSubTaskByID(Long id); // используется для корректной работы кода, на history не влияют

    public Task getTaskUser(Long id); // используется пользователем, именно эти запросы сохраняются в history
    public Epic getEpicUser(Long id); // используется пользователем, именно эти запросы сохраняются в history
    public SubTask getSubTaskUser(Long id); // используется пользователем, именно эти запросы сохраняются в history

    public void createTask(Task task);
    public void createEpic(Epic epic);
    public void createSubTask(SubTask subTask);

    public void deleteTaskByID(Long id);
    public void deleteEpicByID(Long id);
    public void deleteSubTaskByID(Long id);

    public void updateTask(Task task);
    public void updateEpic(Epic epic);
    public void updateSubTask(SubTask subTask);

    public List<SubTask> getSubTasksByEpicId(Long id);

    public List<Task> getHistory();

    public LocalDateTime calcStartTimeEpic (Long id);
    public LocalDateTime calcEndTimeEpic (Long id);
    public Long calcDurationEpic (Long id);

    public Set<Task> getPrioritizedTasks();
    public boolean isDateTimeFree(Task task);

}
