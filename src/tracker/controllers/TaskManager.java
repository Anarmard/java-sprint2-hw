package tracker.controllers;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    public ArrayList<Task> getAllTasks();
    public ArrayList<Epic> getAllEpics();
    public ArrayList<SubTask> getAllSubTasks();

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

    public void updateTask(Task task);
    public void updateEpic(Epic epic);
    public void updateSubTask(SubTask subTask);

    public void deleteTaskByID(Long id);
    public void deleteEpicByID(Long id);
    public void deleteSubTaskByID(Long id);

    public ArrayList<SubTask> getSubTasksByEpicId(Long id);
    public List<Task> getHistory();
}
