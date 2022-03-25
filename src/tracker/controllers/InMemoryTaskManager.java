package tracker.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskStatus;

public class InMemoryTaskManager implements TaskManager{
    private Long id = Long.valueOf(0);
    private HashMap<Long, Task> taskMap = new HashMap<>();
    private HashMap<Long, SubTask> subTaskMap = new HashMap<>();
    private HashMap<Long, Epic> epicMap = new HashMap<>();
    private static HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Получение списка всех задач.
    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : taskMap.values()) {
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>();
        for (Epic epic : epicMap.values()) {
            epicArrayList.add(epic);
        }
        return epicArrayList;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        for (SubTask subTask : subTaskMap.values()) {
            subTaskArrayList.add(subTask);
        }
        return subTaskArrayList;
    }

    // Удаление всех задач.
    @Override
    public void deleteAllTasks() {
        taskMap.clear();
    }

    @Override
    public void deleteAllEpics() {
        epicMap.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTaskMap.clear();
    }

    // Получение по идентификатору.
    @Override
    public Task getTaskByID(Long id) {
        Task task= taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(Long id) {
        Epic epic = epicMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskByID(Long id) {
        SubTask subTask = subTaskMap.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    // Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createTask(Task task) {
        id++;
        task.setId(id);
        taskMap.put(id, task);
    }

    @Override
    public void createEpic(Epic epic) {
        id++;
        epic.setId(id);
        epicMap.put(id, epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        id++;
        subTask.setId(id);
        subTaskMap.put(id, subTask);
        Long idEpic = subTask.getIdEpic();
        Epic epic = getEpicByID(idEpic);
        ArrayList <Long> idListSubTask = epic.getIdListSubTask();
        idListSubTask.add(id);
        epic.setIdListSubTask(idListSubTask);
        updateEpic(epic);
    }

    // Обновление. Новая версия объекта с верным идентификатором передаются в виде параметра.
    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(),task);
    }

    @Override
    public void updateEpic(Epic epic) {
        // проверить статусы у всех Подзадач этого Эпика и потом у самого Эпика
        boolean isDone = true;
        boolean isNew = true;
        if (epic.getIdListSubTask().isEmpty()) {
            isDone = false;
        } else {
            for (Long idSubTask : epic.getIdListSubTask()) {
                // если у одной из подзадач статус NEW значит у эпика не может быть статуса DONE
                if (subTaskMap.get(idSubTask).getStatus() == TaskStatus.NEW) {
                    isDone = false;
                }
                // если у одной из подзадач статус DONE значит у эпика не может быть статуса NEW
                if (subTaskMap.get(idSubTask).getStatus() == TaskStatus.DONE) {
                    isNew = false;
                }
            }
        }

        // по итогам проверок всех подзадач, проставляем статус у самого эпика
        if (isDone && !isNew) {
            epic.setStatus(TaskStatus.DONE);
        } else if (!isDone && isNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        epicMap.put(epic.getId(),epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(),subTask);
        Long idEpic = subTask.getIdEpic(); // сохранили ID Эпика
        Epic epic = getEpicByID(idEpic);
        updateEpic(epic);
    }

    // Удаление по идентификатору.
    @Override
    public void deleteTaskByID(Long id) {
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicByID(Long id) {
        Epic epic = getEpicByID(id);
        epicMap.remove(id);
        ArrayList<Long> subTasks= epic.getIdListSubTask();
        for (Long idSubTask : subTasks) {
            deleteSubTaskByID(idSubTask);
        }
    }

    @Override
    public void deleteSubTaskByID(Long id) {
        SubTask subTask = getSubTaskByID(id);
        Long idEpic = subTask.getIdEpic(); // сохранили ID Эпика
        subTaskMap.remove(id); // удалили подзадачу

        // надо удалить ID подзадачи из списка idListSubTask
        if (getEpicByID(idEpic) != null) { // это условие нужно, чтобы проверить, что эпик не был удален
            Epic epic = getEpicByID(idEpic);
            epic.getIdListSubTask().remove(id);
            updateEpic(epic);
        }
    }

    // Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<SubTask> getSubTasksByEpicId(Long id) {
        Epic epic = getEpicByID(id);
        ArrayList<SubTask> subTasksArrayList = new ArrayList<>();
        for (long idSubTask : epic.getIdListSubTask()) {
            SubTask subTask = subTaskMap.get(idSubTask);
            subTasksArrayList.add(subTask);
        }
        return subTasksArrayList;
    }


}
