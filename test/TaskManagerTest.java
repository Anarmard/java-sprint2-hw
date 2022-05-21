import org.junit.jupiter.api.Test;
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

    // Стандартный кейс task, epic, subtask
    @Test
    void taskCreateAndGet() {
        Task task = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        taskManager.createTask(task);

        final Task taskInCorrectID = taskManager.getTaskByID(2L); // С неверным идентификатором задачи
        assertNull(taskInCorrectID, "Несуществующая задача найдена.");

        final Task savedTask = taskManager.getTaskByID(1L);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void subTaskCreateAndGetEpic() {
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 1L);
        taskManager.createSubTask(subTask13);

        final SubTask subTaskInCorrectID = taskManager.getSubTaskByID(3L); // С неверным идентификатором задачи
        assertNull(subTaskInCorrectID, "Несуществующая подзадача найдена.");

        final SubTask savedSubTask = taskManager.getSubTaskByID(2L);
        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask13, savedSubTask, "Подзадачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи на возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask13, subTasks.get(0), "Задачи не совпадают.");

        final long epicId = subTask13.getIdEpic();
        assertEquals(epic1.getId(), epicId, "ID эпиков не совпадают.");
    }

    @Test
    void epicCreateAndGet() {
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

        final Epic epicInCorrectID = taskManager.getEpicByID(2L); // С неверным идентификатором задачи
        assertNull(epicInCorrectID, "Несуществующий Epic найден.");

        final Epic savedEpic = taskManager.getEpicByID(1L);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество Эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");

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

    /*
    По ТЗ нужны тесты
    "Граничные условия:
    a. Пустой список задач.
    b. Эпик без подзадач.
    c. Пустой список истории."
    Пункт b вижу, а пункт а и с?
    */

    @Test
    void emptyTaskManagerAndHistoryManager() {
        final List<Task> taskBlankList = taskManager.getAllTasks();
        final List<Epic> epicBlankList = taskManager.getAllEpics();
        final List<SubTask> subTaskBlankList =taskManager.getAllSubTasks();
        assertEquals(0, taskBlankList.size(), "Пустой список задач не пустой.");
        assertEquals(0, epicBlankList.size(), "Пустой список эпиков не пустой.");
        assertEquals(0, subTaskBlankList.size(), "Пустой список подзадач не пустой.");

        final List<Task> historyBlankList = taskManager.getHistory();
        assertEquals(0, historyBlankList.size(), "Пустая история просмотров не пустая.");
    }


    /* перенесли тестирование в InMemoryHistoryManagerTest (где тестируются все методы History)
    //============================Проверка формирования истории просмотров============================
    @Test
    void checkGetHistory() {
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("2Task", "2TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 10, 0), 180L);
        taskManager.createTask(task2);
        Task task3 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 60L);
        taskManager.createTask(task3);

        // создание Эпика с 3-мя подзадачами
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        taskManager.createEpic(epic1);
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 0), 30L, 4L);
        taskManager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 2, 10, 0), 90L,4L);
        taskManager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 4L);
        taskManager.createSubTask(subTask13);

        final List<Task> historyBlank = taskManager.getHistory();
        assertEquals(0, historyBlank.size(), "История просмотров не пустая.");

        //создание истории
        taskManager.getSubTaskUser(5L);
        taskManager.getEpicUser(4L);
        taskManager.getTaskUser(1L);
        taskManager.getSubTaskUser(2L);
        taskManager.getTaskUser(3L);
        taskManager.getTaskUser(2L);
        taskManager.getTaskUser(1L);

        historyComplete = taskManager.getHistory();

        assertNotNull(historyComplete, "История просмотров пустая.");
        assertEquals(5, historyComplete.size(), "Неверное количество уникальных просмотренных задач.");

        taskManager.deleteTaskByID(1L);
        historyComplete = taskManager.getHistory();
        assertEquals(4, historyComplete.size(), "История не уменьшилась после удаления 1 задачи.");
    }
    */
}