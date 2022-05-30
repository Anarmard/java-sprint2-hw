import org.junit.jupiter.api.Test;
import tracker.controllers.HistoryManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected final T taskManager;
    public TaskManagerTest(T taskManager){
        this.taskManager = taskManager;
    }

    //============================ Тесты на методы с TASK ============================
    @Test
    void emptyResultOnWrongIDForGetTask() { // c неверным идентификатором TASK
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        taskManager.createTask(task1);

        final Task taskInCorrectID = taskManager.getTaskByID(2L);
        assertNull(taskInCorrectID, "Несуществующая задача найдена.");
    }

    @Test
    void successCreateAndGetTaskById() { // получение TASK через ID
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        taskManager.createTask(task1);

        final Task savedTask = taskManager.getTaskByID(1L);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
    }

    @Test
    void successGetAllTasks() { // получение всех TASK
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("2Task", "2TaskDescription", 0L, TaskStatus.NEW);
        taskManager.createTask(task2);

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    //============================ Тесты на методы с SUBTASK ============================
    @Test
    void emptyResultOnWrongIDForGetSubTask() { // c неверным идентификатором SUBTASK
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 1L);
        taskManager.createSubTask(subTask13);

        final SubTask subTaskInCorrectID = taskManager.getSubTaskByID(3L); // С неверным идентификатором SUBTASK
        assertNull(subTaskInCorrectID, "Несуществующая подзадача найдена.");
    }

    @Test
    void successCreateAndGetSubTaskById() { // получение SUBTASK через ID
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 1L);
        taskManager.createSubTask(subTask13);

        final SubTask savedSubTask = taskManager.getSubTaskByID(2L);
        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask13, savedSubTask, "Подзадачи не совпадают.");
    }

    @Test
    void successGetAllSubTask() { // получение всех SUBTASK
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 1L);
        taskManager.createSubTask(subTask13);

        final List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertNotNull(subTasks, "Подзадачи на возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask13, subTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void successFromSubTaskGetEpic() { // получение ID Epic через SUBTASK
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 1L);
        taskManager.createSubTask(subTask13);

        final long epicId = subTask13.getIdEpic();
        assertEquals(epic1.getId(), epicId, "ID эпиков не совпадают.");
    }

    //============================ Тесты на методы с EPIC ============================
    @Test
    void emptyResultOnWrongIDForGetEpic() { // c неверным идентификатором EPIC
        final Epic epicInCorrectID = taskManager.getEpicByID(2L); // С неверным идентификатором задачи
        assertNull(epicInCorrectID, "Несуществующий Epic найден.");
    }

    @Test
    void successCreateAndGetEpicById() { // получение EPIC через ID
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 0), 30L, 1L);
        taskManager.createSubTask(subTask11);

        final Epic savedEpic = taskManager.getEpicByID(1L);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void successGetAllEpics() { // получение всех EPIC
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic ("2Epic", "2EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic2);

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }


    @Test
    void successGetAllSubTaskFromEpic() { // получение всех SUBTASK у EPIC
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 0), 30L, 1L);
        taskManager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 2, 10, 0), 90L,1L);
        taskManager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 1L);
        taskManager.createSubTask(subTask13);

        final List<SubTask> subTasks = taskManager.getSubTasksByEpicId(epic1.getId());
        final List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        assertEquals(allSubTasks, subTasks, "Подзадачи у Эпика не совпадают.");
    }

    //============================Доп тесты на Epic============================
    // Пустой список подзадач
    @Test
    void epicWithNoSubtasks() {
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);

        final Epic savedEpic = taskManager.getEpicByID(1L);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubTasksByEpicId(epic1.getId());
        final List<SubTask> blankList = new ArrayList<>();
        assertEquals(blankList, subTasks, "У Эпика есть подзадача.");
    }

    // тестируем расчет статусов у Эпика
    @Test
    void epicCheckStatus() {
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);

        // Все подзадачи со статусом NEW
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,1L);
        taskManager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 0L, TaskStatus.NEW,1L);
        taskManager.createSubTask(subTask12);
        assertEquals(TaskStatus.NEW, epic1.getStatus(), "У Эпика статус не NEW.");

        // Все подзадачи со статусом NEW и DONE
        taskManager.updateSubTask(new SubTask("11SubTask", "11SubTaskDescription", 2L, TaskStatus.NEW,1L));
        taskManager.updateSubTask(new SubTask("12SubTask", "12SubTaskDescription", 3L, TaskStatus.DONE,1L));
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "У Эпика статус не IN_PROGRESS.");

        // Все подзадачи со статусом DONE
        taskManager.updateSubTask(new SubTask("11SubTask", "11SubTaskDescription", 2L, TaskStatus.DONE,1L));
        assertEquals(TaskStatus.DONE, epic1.getStatus(), "У Эпика статус не DONE.");

        // Все подзадачи со статусом IN_PROGRESS
        taskManager.updateSubTask(new SubTask("11SubTask", "11SubTaskDescription", 2L, TaskStatus.IN_PROGRESS,1L));
        taskManager.updateSubTask(new SubTask("12SubTask", "12SubTaskDescription", 3L, TaskStatus.IN_PROGRESS,1L));
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "У Эпика статус не IN_PROGRESS.");
    }

    //============================Тесты на время============================
    // тест на пересечение времени при создании задач
    @Test
    void checkDateTimeIsFree() {
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 10, 0), 90L);
        taskManager.createTask(task1);
        Task task2 = new Task("2Task", "2TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 180L);
        taskManager.createTask(task2);

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    // тест на расчет дат для Эпика по датам его подзадач
    @Test
    void checkEpicCalcDateTime() {
        Epic epic1 = new Epic("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 0), 30L, 1L);
        taskManager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 2, 10, 0), 90L, 1L);
        taskManager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 1L);
        taskManager.createSubTask(subTask13);

        assertEquals(subTask11.getStartTime(), taskManager.calcStartTimeEpic(1L), "У Эпика неверная дата начала.");
        assertEquals(subTask12.getEndTime(subTask12.getStartTime(), subTask12.getDuration()), taskManager.calcEndTimeEpic(1L), "У Эпика неверная дата окончания.");
        assertEquals((subTask11.getDuration() + subTask12.getDuration()), taskManager.calcDurationEpic(1L), "У Эпика неверная длительность.");
    }


    // проверка на сортировку 3-х задач по времени
    @Test
    void checkPrioritizedTasks() {
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("2Task", "2TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 10, 0), 180L);
        taskManager.createTask(task2);
        Task task3 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 60L);
        taskManager.createTask(task3);

        Set<Task> prioritizedTasksSet = taskManager.getPrioritizedTasks();
        List<Task> prioritizedTasksList = new ArrayList<Task>();
        prioritizedTasksList.addAll(prioritizedTasksSet);

        assertEquals(task3, prioritizedTasksList.get(0), "Самая ранняя задача.");
        assertEquals(task2, prioritizedTasksList.get(1), "Самая поздняяя задача.");
        assertEquals(task1, prioritizedTasksList.get(2), "Задача без срока не в конце списка.");
    }

    @Test
    void emptyTaskManager() {
        final List<Task> taskBlankList = taskManager.getAllTasks();
        final List<Epic> epicBlankList = taskManager.getAllEpics();
        final List<SubTask> subTaskBlankList =taskManager.getAllSubTasks();
        assertEquals(0, taskBlankList.size(), "Пустой список задач не пустой.");
        assertEquals(0, epicBlankList.size(), "Пустой список эпиков не пустой.");
        assertEquals(0, subTaskBlankList.size(), "Пустой список подзадач не пустой.");
    }

    @Test
    void emptyHistoryManager() {
        TaskManager taskManager = Managers.getDefault();
        final List<Task> historyBlankList = taskManager.getHistory();
        assertEquals(0, historyBlankList.size(), "Пустая история просмотров не пустая.");
    }

}