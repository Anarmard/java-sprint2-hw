import org.junit.jupiter.api.Test;
import tracker.controllers.FileBackedTasksManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskStatus;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tracker.controllers.FileBackedTasksManager.loadFromFile;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager("TasksCSV.csv"));
    }

    private static String fileName = "TasksCSV.csv";
    private static final String pathDr = "C://Users//Anar//dev//java-sprint2-hw";
    private static final Path pathTaskManager = Paths.get(pathDr, fileName); // создаем объект типа Path (содержит полный путь к файлу)
    private static final FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(fileName);

    @Test
    void checkSaveAndLoad() {
        final List<Task> taskBlankList = fileBackedTasksManager.getAllTasks();
        final List<Epic> epicBlankList = fileBackedTasksManager.getAllEpics();
        final List<SubTask> subTaskBlankList =fileBackedTasksManager.getAllSubTasks();
        assertEquals(0, taskBlankList.size(), "Пустой список задач не пустой.");
        assertEquals(0, epicBlankList.size(), "Пустой список эпиков не пустой.");
        assertEquals(0, subTaskBlankList.size(), "Пустой список подзадач не пустой.");

        // создание 3-х задач
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        fileBackedTasksManager.createTask(task1);
        Task task2 = new Task("2Task", "2TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 10, 0), 180L);
        fileBackedTasksManager.createTask(task2);
        Task task3 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 60L);
        fileBackedTasksManager.createTask(task3);

        // создание Эпика с 3-мя подзадачами
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        fileBackedTasksManager.createEpic(epic1);
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 0), 30L, 4L);
        fileBackedTasksManager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 2, 10, 0), 90L,4L);
        fileBackedTasksManager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 4L);
        fileBackedTasksManager.createSubTask(subTask13);

        // создание Эпика без подзадач
        Epic epic2 = new Epic ("2Epic", "2EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        fileBackedTasksManager.createEpic(epic2);

        // создание задач с пересечением времени
        Task task4 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 30), 60L);
        fileBackedTasksManager.createTask(task4);
        SubTask subTask21 = new SubTask("21SubTask", "21SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 90L,8L);
        fileBackedTasksManager.createSubTask(subTask21);

        List<Task> historyList = fileBackedTasksManager.getHistory();
        assertEquals("[]", historyList.toString(), "Пустая история просмотров не пустая");

        fileBackedTasksManager.getSubTaskUser(5L);
        fileBackedTasksManager.getEpicUser(4L);
        fileBackedTasksManager.getTaskUser(1L);
        fileBackedTasksManager.getSubTaskUser(5L);
        fileBackedTasksManager.getEpicUser(8L);
        fileBackedTasksManager.getTaskUser(2L);
        fileBackedTasksManager.getTaskUser(2L);
        fileBackedTasksManager.getSubTaskUser(6L);
        fileBackedTasksManager.getEpicUser(8L);
        fileBackedTasksManager.getSubTaskUser(7L);
        fileBackedTasksManager.getTaskUser(2L);
        fileBackedTasksManager.getTaskUser(1L);
        fileBackedTasksManager.getEpicUser(8L);

        FileBackedTasksManager loadedTaskManager = loadFromFile(pathTaskManager);
        historyList = loadedTaskManager.getHistory();
        List<Long> historyListID = new ArrayList<>();
        for (Task task : historyList) {
            historyListID.add(task.getId());
        }
        assertEquals("[4, 5, 6, 7, 2, 1, 8]", historyListID.toString(), "Неверная история просмотров");

    }
}