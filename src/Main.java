import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.model.SubTask;
import tracker.model.Epic;
import tracker.model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        createAllTasks(manager);
        System.out.println("\n===Печать всех задач/эпиков/подзадач изначальный===");
        printAllTasks(manager);

        int input = printMenu();
        if (input == 1) {
            checkMethods(manager);
        } else if (input == 2) {
            checkHistory(manager);
        } else if (input == 3) {
            System.out.println(manager.getPrioritizedTasks());
        } else {
            System.out.println("неверная команда");
        }

    }

    public static int printMenu() {
        System.out.println("\n Что хотим проверить? \n " +
                "1-методы-TaskManager \n " +
                "2-историю-HistoryManager\n " +
                "3-отсортировать список задач по дате начала");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    // метод для создания заданного набора Task, Epic, Subtask. Для проверки
    public static void createAllTasks (TaskManager manager) {
        // создание 3-х задач
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        manager.createTask(task1);
        Task task2 = new Task("2Task", "2TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 10, 0), 180L);
        manager.createTask(task2);
        Task task3 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 60L);
        manager.createTask(task3);

        // создание Эпика с 3-мя подзадачами
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic1);
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 0), 30L, 4L);
        manager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 2, 10, 0), 90L,4L);
        manager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 4L);
        manager.createSubTask(subTask13);

        // создание Эпика без подзадач
        Epic epic2 = new Epic ("2Epic", "2EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic2);

        // создание задач с пересечением времени
        Task task4 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 30), 60L);
        manager.createTask(task4);
        SubTask subTask21 = new SubTask("21SubTask", "21SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 90L,8L);
        manager.createSubTask(subTask21);

    }

    // Метод для печати всех Task, Epic, Subtask
    public static void printAllTasks (TaskManager manager) {
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
    }

    // Метод для проверки истории просмотров задач
    public static void checkHistory(TaskManager manager) {
        createHistory(manager);

        manager.deleteTaskByID(1L);
        manager.deleteSubTaskByID(5L);
        System.out.println("\n===Печать истории просмотров после удаления задачи и подзадачи===");
        System.out.println(manager.getHistory());

        manager.deleteEpicByID(4L);
        System.out.println("\n===Печать истории просмотров после удаления эпика===");
        System.out.println(manager.getHistory()); // проверить, что подзадачи 6 и 7 тоже удалились
    }

    public static void createHistory(TaskManager manager) {
        manager.getSubTaskUser(5L);
        manager.getEpicUser(4L);
        manager.getTaskUser(1L);
        manager.getSubTaskUser(5L);
        manager.getEpicUser(8L);
        manager.getTaskUser(2L);
        manager.getTaskUser(2L);
        manager.getSubTaskUser(6L);
        manager.getEpicUser(8L);
        manager.getSubTaskUser(7L);
        manager.getTaskUser(2L);
        manager.getTaskUser(1L);
        manager.getEpicUser(8L);

        System.out.println("\n===Печать истории просмотров===");
        System.out.println(manager.getHistory());
    }

    public static void checkMethods (TaskManager manager) {
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 6L, TaskStatus.DONE, 4L);
        manager.updateSubTask(subTask12);
        Task task1 = new Task("1Task", "1TaskDescription", 1L, TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.println("\n===Печать всех задач/эпиков/подзадач после обновления статусов===");
        printAllTasks(manager);

        manager.deleteSubTaskByID(6L);
        manager.deleteTaskByID(2L);
        System.out.println("\n===Печать всех задач/эпиков/подзадач после удаления===");
        // проверить, что подзадача удалилась из списка подзадач эпика
        printAllTasks(manager);

        manager.deleteAllTasks();
        manager.deleteEpicByID(4L);
        System.out.println("\n===Печать всех задач/эпиков/подзадач после удаления===");
        // проверить, что подзадачи удалились
        printAllTasks(manager);
    }
}

