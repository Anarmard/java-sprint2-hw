import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;

import tracker.controllers.HistoryManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.model.SubTask;
import tracker.model.Epic;
import tracker.controllers.InMemoryTaskManager;
import tracker.model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // создание 2-х задач
        Task task1 = new Task("Продукты", "Заехать в магазин", 0L, TaskStatus.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Ремонт", "Покрасить стену", 0L, TaskStatus.NEW);
        manager.createTask(task2);

        // создание Эпика с 3-мя подзадачами
        Epic epic1 = new Epic ("Уборка дома", "Еженедельная процедура", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic1);
        SubTask subTask11 = new SubTask("Пыль", "Протереть пыль", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("Стирка", "Постирать вещи", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("Посуда", "Помыть посуду", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask13);

        // создание Эпика без подзадач
        Epic epic2 = new Epic ("Спорт", "Улучшить здоровье", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic2);

        // печать всех типов задач
        System.out.println("\nПечать всех задач/эпиков/подзадач без изменения:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        manager.getSubTaskUser(4L);
        manager.getTaskUser(1L);
        manager.getEpicUser(7L);
        manager.getTaskUser(2L);
        manager.getEpicUser(3L);
        manager.getSubTaskUser(4L);
        manager.getTaskUser(2L);
        manager.getSubTaskUser(5L);
        manager.getEpicUser(7L);
        manager.getSubTaskUser(6L);
        manager.getTaskUser(2L);
        manager.getTaskUser(1L);
        manager.getEpicUser(7L);

        System.out.println("\nПечать истории просмотров:");
        System.out.println(manager.getHistory());

    }
}