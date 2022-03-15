package tracker.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

public class Manager {
    private Long id = Long.valueOf(0);
    HashMap<Long, Task> taskMap = new HashMap<>();
    HashMap<Long, SubTask> subTaskMap = new HashMap<>();
    HashMap<Long, Epic> epicMap = new HashMap<>();

    // Получение списка всех задач.
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : taskMap.values()) {
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>();
        for (Epic epic : epicMap.values()) {
            epicArrayList.add(epic);
        }
        return epicArrayList;
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        for (SubTask subTask : subTaskMap.values()) {
            subTaskArrayList.add(subTask);
        }
        return subTaskArrayList;
    }

    // Удаление всех задач.
    public void deleteAllTasks() {
        taskMap.clear();
    }

    public void deleteAllEpics() {
        epicMap.clear();
    }

    public void deleteAllSubTasks() {
        subTaskMap.clear();
    }

    // Получение по идентификатору.
    public Task getTaskByID(Long id) {
        Task task;
        return task = taskMap.get(id);
    }

    public Epic getEpicByID(Long id) {
        Epic epic;
        return epic = epicMap.get(id);
    }

    public SubTask getSubTaskByID(Long id) {
        SubTask subTask;
        return subTask = subTaskMap.get(id);
    }

    // Создание. Сам объект должен передаваться в качестве параметра.
    public void createTask(Task task) {
        id++;
        task.setId(id);
        taskMap.put(id, task);
    }

    public void createEpic(Epic epic) {
        id++;
        epic.setId(id);
        epicMap.put(id, epic);
    }

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
    public void updateTask(Task task) {
        taskMap.put(task.getId(),task);
    }

    public void updateEpic(Epic epic) {
        // проверить статусы у всех Подзадач этого Эпика и потом у самого Эпика
        boolean isDone = true;
        boolean isNew = true;
        if (epic.getIdListSubTask().isEmpty()) {
            isDone = false;
        } else {
            for (Long idSubTask : epic.getIdListSubTask()) {
                String statusSubTask = subTaskMap.get(idSubTask).getStatus();
                if (statusSubTask.equals("NEW")) {
                    isDone = false;
                }
                if (statusSubTask.equals("DONE")) {
                    isNew = false;
                }
            }
        }

        if (isDone && !isNew) {
            epic.setStatus("DONE");
        } else if (!isDone && isNew) {
            epic.setStatus("NEW");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
        epicMap.put(epic.getId(),epic);
    }

    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(),subTask);
        Long idEpic = subTask.getIdEpic(); // сохранили ID Эпика
        Epic epic = getEpicByID(idEpic);
        updateEpic(epic);
    }

    // Удаление по идентификатору.
    public void deleteTaskByID(Long id) {
        taskMap.remove(id);
    }

    public void deleteEpicByID(Long id) {
        Epic epic = getEpicByID(id);
        epicMap.remove(id);
        ArrayList<Long> subTasks= epic.getIdListSubTask();
        for (Long idSubTask : subTasks) {
            deleteSubTaskByID(idSubTask);
        }
    }

    public void deleteSubTaskByID(Long id) {
        SubTask subTask = getSubTaskByID(id);
        Long idEpic = subTask.getIdEpic(); // сохранили ID Эпика
        subTaskMap.remove(id); // удалили подзадачу

        // надо удалить ID подзадачи из списка idListSubTask
        Epic epic = getEpicByID(idEpic);
        if (epic != null) { // это условие нужно, чтобы проверить, что эпик не был удален
            epic.getIdListSubTask().remove(id);
            updateEpic(epic);
        }
    }

    // Получение списка всех подзадач определённого эпика.
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
