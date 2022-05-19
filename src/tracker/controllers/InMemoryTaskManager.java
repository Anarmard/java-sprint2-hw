package tracker.controllers;

import java.time.LocalDateTime;
import java.util.*;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskStatus;

public class InMemoryTaskManager implements TaskManager{
    private Long id = Long.valueOf(0);
    private final Map<Long, Task> taskMap = new HashMap<>();
    private final Map<Long, SubTask> subTaskMap = new HashMap<>();
    private final Map<Long, Epic> epicMap = new HashMap<>();
    private static final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Comparator<Task> comparator = new Comparator<>() {
        @Override
        public int compare(Task t1, Task t2) { // сортируем по getStartTime, если пусто ранжировать в конце по id
            if ((t1.getStartTime() != null) && (t2.getStartTime() != null)) {
                return t1.getStartTime().compareTo(t2.getStartTime());
            } else if ((t1.getStartTime() == null) && (t2.getStartTime() == null)) {
                return t1.getId().compareTo(t2.getId());
            } else if (t1.getStartTime() == null) {
                return 1;
            } else
                return -1;
        }
    };

    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    // Можно добавить модификатор final, так как поле никогда не должно меняться. Тоже касается всех Map.
    // К тому же будет лучше, если у полей будет тип данных Map вместо HashMap. Предпочтительнее обращаться
    // через интерфейс, чем через имплементацию.

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean isDateTimeFree(Task task) {
        if ((task.getStartTime() != null) && (task.getDuration() != null)) {
            for (Task taskExist : getPrioritizedTasks()) {
                if ((taskExist.getStartTime() != null) && (taskExist.getDuration() != null)) {
                    LocalDateTime taskStartDate = task.getStartTime();
                    LocalDateTime taskEndDate = task.getEndTime(taskStartDate, task.getDuration());
                    LocalDateTime existTaskStartDate = taskExist.getStartTime();
                    LocalDateTime existTaskEndDate = taskExist.getEndTime(existTaskStartDate, taskExist.getDuration());

                    if ((existTaskStartDate.isAfter(taskStartDate)) && existTaskStartDate.isBefore(taskEndDate)) {
                        return false; // в указанный период уже запланирован старт другой задачи
                    }

                    if ((existTaskEndDate.isAfter(taskStartDate)) && existTaskEndDate.isBefore(taskEndDate)) {
                        return false; // в указанный период уже запланировано окончание другой задачи
                    }

                    if ((existTaskStartDate.isBefore(taskStartDate)) && existTaskEndDate.isAfter(taskEndDate)) {
                        return false; // весь указанный период уже занят выполнением другой задачи
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Получение списка всех задач.
    @Override
    public List<Task> getAllTasks() {
        // лучше использовать интерфейсы вместо реализации в сигнатуре. public List<Task> getAllTasks()
        // лучше чем завязка на конкретную реализацию ArrayList.
        List<Task> taskArrayList = new ArrayList<>(taskMap.values());
        // new ArrayList(taskMap.values()) сделает ровно ту же задачу всего одной строкой
        // for (Task task : taskMap.values()) {  taskArrayList.add(task); }
        return taskArrayList;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicArrayList = new ArrayList<>(epicMap.values());
        return epicArrayList;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        List<SubTask> subTaskArrayList = new ArrayList<>(subTaskMap.values());
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
        if (isDateTimeFree(task)) {
            if (task.getId() == 0L) {
                id++;
                task.setId(id);
                taskMap.put(id, task);
            }
            taskMap.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            System.out.println("Указанный период времени уже занят другой задачей. " +
                    "Укажите другую дату начала для задачи: "+ task.getName());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == 0L) {
            id++;
            epic.setId(id);
        }
        epicMap.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        if (isDateTimeFree(subTask)) {
            if (subTask.getId() == 0L) {
                id++;
                subTask.setId(id);
            }
            subTaskMap.put(subTask.getId(), subTask);
            prioritizedTasks.add(subTask);
            Long idEpic = subTask.getIdEpic();
            Epic epic = getEpicByID(idEpic);
            if (epic.getIdListSubTask() == null) {
                ArrayList<Long> idListSubTask = new ArrayList<>();
                idListSubTask.add(subTask.getId());
                epic.setIdListSubTask(idListSubTask);
            } else {
                ArrayList<Long> idListSubTask = epic.getIdListSubTask();
                idListSubTask.add(subTask.getId());
            }

            // обновляем startTime endTime duration
            epic.setStartTime(calcStartTimeEpic(epic.getId()));
            epic.setEndTimeEpic(calcEndTimeEpic(epic.getId()));
            epic.setDuration(calcDurationEpic(epic.getId()));
            prioritizedTasks.add(epic);
        } else {
            System.out.println("Указанный период времени уже занят другой задачей. " +
                    "Укажите другую дату начала для задачи: "+ subTask.getName());
        }
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

        // обновляем startTime endTime duration
        epic.setStartTime(calcStartTimeEpic(epic.getId()));
        epic.setEndTimeEpic(calcEndTimeEpic(epic.getId()));
        epic.setDuration(calcDurationEpic(epic.getId()));

        // epicMap.put(epic.getId(), epic); - Если проводятся операции на объекте Epic, который уже хранится в Map,
        // то необязательно заново сохранять объект в Map.
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
            epic.setStartTime(calcStartTimeEpic(idEpic)); // заново посчитали startTime, т.к. subTask был удален
            epic.setEndTimeEpic(calcEndTimeEpic(idEpic)); // заново посчитали endTime, т.к. subTask был удален
            epic.setDuration(calcDurationEpic(idEpic)); // заново посчитали duration, т.к. subTask был удален
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

    // Расчет для Epic даты начала, как самая ранняя дата начала из всех Subtask
    @Override
    public LocalDateTime calcStartTimeEpic (Long id) {
        Epic epic = getEpicByID(id);
        if (epic.getIdListSubTask().isEmpty()) {
            return null;
        }
        List<SubTask> subTasksArrayList = getSubTasksByEpicId(id);
        LocalDateTime startTime = null;
        boolean isFirstNotNullDate = true; // если найдем хоть одно не пустое поле startTime у SubTask-ов
        for (SubTask subTask : subTasksArrayList) {
            if (subTask.getStartTime()!= null) {
                if (isFirstNotNullDate) { // первое же не пустое поле устаналиваем в качестве ориентира
                    startTime = subTask.getStartTime();
                    isFirstNotNullDate = false;
                } else if (subTask.getStartTime().isBefore(startTime)) {
                    startTime = subTask.getStartTime();
                }
            }
        }
        return startTime;
    }

    // Расчет для Epic даты окончания, как самая поздняя дата окончания из всех Subtask
    @Override
    public LocalDateTime calcEndTimeEpic (Long id) {
        Epic epic = getEpicByID(id);
        if (epic.getIdListSubTask().isEmpty()) {
            return null;
        }
        List<SubTask> subTasksArrayList = getSubTasksByEpicId(id);
        LocalDateTime endTime = null;
        boolean isFirstNotNullDate = true; // если найдем хоть одно не пустое поле endTime у SubTask-ов
        for (SubTask subTask : subTasksArrayList) {
            if (subTask.getEndTime(subTask.getStartTime(), subTask.getDuration())!= null) {
                if (isFirstNotNullDate) { // первое же не пустое поле устаналиваем в качестве ориентира
                    endTime = subTask.getEndTime(subTask.getStartTime(), subTask.getDuration());
                    isFirstNotNullDate = false;
                }else if (subTask.getEndTime(subTask.getStartTime(), subTask.getDuration()).isAfter(endTime)) {
                    endTime = subTask.getEndTime(subTask.getStartTime(), subTask.getDuration());
                }
            }
        }
        return endTime;
    }

    // Расчет для Epic продолжительности, как сумма продолжительности всех Subtask
    @Override
    public Long calcDurationEpic(Long id) {
        Epic epic = getEpicByID(id);
        if (epic.getIdListSubTask().isEmpty()) {
            return null;
        }
        List<SubTask> subTasksArrayList = getSubTasksByEpicId(id);
        Long duration = null;
        boolean isFirstNotNullDuration = true; // если найдем хоть одно не пустое поле duration у SubTask-ов
        for (SubTask subTask : subTasksArrayList) {
            if (subTask.getDuration()!= null) {
                if (isFirstNotNullDuration) { // нашли не пустое поле, значит duration у Epic уже не может быть null
                    duration = 0L;
                    isFirstNotNullDuration = false;
                }
                duration += subTask.getDuration();
            }
        }
        return duration;
    }
}
