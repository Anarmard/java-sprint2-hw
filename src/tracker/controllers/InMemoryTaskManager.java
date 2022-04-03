package tracker.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskStatus;

public class InMemoryTaskManager implements TaskManager{
    private Long id = Long.valueOf(0);
    private final Map<Long, Task> taskMap = new HashMap<>();
    private final Map<Long, SubTask> subTaskMap = new HashMap<>();
    private final Map<Long, Epic> epicMap = new HashMap<>();
    private static HistoryManager historyManager = Managers.getDefaultHistory();

    // Можно добавить модификатор final, так как поле никогда не должно меняться. Тоже касается всех Map.
    // К тому же будет лучше, если у полей будет тип данных Map вместо HashMap. Предпочтительнее обращаться
    // через интерфейс, чем через имплементацию.

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Получение списка всех задач.
    @Override
    public List<Task> getAllTasks() {
        // лучше использовать интерфейсы вместо реализации в сигнатуре. public List<Task> getAllTasks()
        // лучше чем завязка на конкретную реализацию ArrayList. Лучше сверить все методы в данном классе
        // и проверить на это
        List<Task> taskArrayList = new ArrayList<>(taskMap.values());
        // Здесь и во остальных схожих методах можно улучшить код создание List из Map.
        //new ArrayList(taskMap.values()) сделает ровно ту же задачу всего одной строкой
        // for (Task task : taskMap.values()) {
        //    taskArrayList.add(task);
        // }
        return taskArrayList;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicArrayList = new ArrayList<>(epicMap.values());
        //for (Epic epic : epicMap.values()) {
        //    epicArrayList.add(epic);
        //}
        return epicArrayList;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTaskArrayList = new ArrayList<>(subTaskMap.values());
        //for (SubTask subTask : subTaskMap.values()) {
        //    subTaskArrayList.add(subTask);
        //}
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
        return taskMap.get(id);
    }

    @Override
    public Task getTaskUser(Long id) { // в history сохраняются только те задачи, вызванные user
        Task task= taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(Long id) {
        return epicMap.get(id);
    }

    @Override
    public Epic getEpicUser(Long id) { // в history сохраняются только те задачи, вызванные user
        Epic epic = epicMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskByID(Long id) {
        return subTaskMap.get(id);
    }

    @Override
    public SubTask getSubTaskUser(Long id) { // в history сохраняются только те задачи, вызванные user
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
        // epic.setIdListSubTask(idListSubTask); - !операция добавления элемента в List была совершена на том же
        // объекте List, что хранится в объекте Epic!
        // updateEpic(epic); // - спасибо за комментарий, теперь буду знать
    }

    // Обновление. Новая версия объекта с верным идентификатором передаются в виде параметра.
    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
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
        // epicMap.put(epic.getId(), epic); - Если проводятся операции на объекте Epic, который уже хранится в Map,
        // то необязательно заново сохранять объект в Map.
        // спасибо за комментарий, понял, постараюсь учитывать в будущем
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        Long idEpic = subTask.getIdEpic(); // сохранили ID Эпика
        Epic epic = getEpicByID(idEpic);
        updateEpic(epic);
    }

    // Удаление по идентификатору.
    @Override
    public void deleteTaskByID(Long id) {
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicByID(Long id) {
        Epic epic = getEpicByID(id);
        epicMap.remove(id);
        ArrayList<Long> subTasks= epic.getIdListSubTask();
        for (Long idSubTask : subTasks) {
            deleteSubTaskByID(idSubTask);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskByID(Long id) {
        SubTask subTask = getSubTaskByID(id);
        Long idEpic = subTask.getIdEpic(); // сохранили ID Эпика
        subTaskMap.remove(id); // удалили подзадачу
        historyManager.remove(id);

        // надо удалить ID подзадачи из списка idListSubTask
        if (getEpicByID(idEpic) != null) { // это условие нужно, чтобы проверить, что эпик не был удален
            Epic epic = getEpicByID(idEpic);
            epic.getIdListSubTask().remove(id);
            updateEpic(epic);
        }
    }

    // Получение списка всех подзадач определённого эпика.
    @Override
    public List<SubTask> getSubTasksByEpicId(Long id) {
        Epic epic = getEpicByID(id);
        List<SubTask> subTasksArrayList = new ArrayList<>();
        for (long idSubTask : epic.getIdListSubTask()) {
            SubTask subTask = subTaskMap.get(idSubTask);
            subTasksArrayList.add(subTask);
        }
        return subTasksArrayList;
    }


}
