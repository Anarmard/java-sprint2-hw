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

        // создание Эпика с 2-мя подзадачами
        Epic epic1 = new Epic ("Уборка дома", "Еженедельная процедура", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic1);
        SubTask subTask11 = new SubTask("Полы", "Помыть полы", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("Стирка", "Постирать вещи", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask12);

        // создание Эпика с 1-й подзадачой
        Epic epic2 = new Epic ("Спорт", "Улучшить здоровье", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic2);
        SubTask subTask21 = new SubTask("Гимнастика", "Утренняя гимнастика", 0L, TaskStatus.NEW, 6L);
        manager.createSubTask(subTask21);

        // печать всех типов задач
        System.out.println("\nПечать всех задач/эпиков/подзадач без изменения:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        // Изменение статуса у задачи task1 и подзадачи subTask11
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        subTask11.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask11);
        System.out.println("\nПечать всех задач/эпиков/подзадач после обновления статусов:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics()); // тут важно проверить, что статус у эпика поменялся - ок
        System.out.println(manager.getAllSubTasks());

        //Удаление задачи task2, эпика epic2, и подзадачи subTask12
        manager.deleteTaskByID(2L);
        manager.deleteEpicByID(6L);
        manager.deleteSubTaskByID(5L);
        System.out.println("\nПечать всех задач/эпиков/подзадач после удалений:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics()); // тут важно проверить, что статус у эпика поменялся - ок
        System.out.println(manager.getAllSubTasks());

        System.out.println("\nПечать задачи/эпика/подзадачи по введеному ID:");
        System.out.println(manager.getSubTasksByEpicId(3L));


        manager.getSubTaskByID(4L);
        manager.getEpicByID(3L);
        manager.getTaskByID(1L);
        System.out.println("\nПечать истории просмотров:");
        System.out.println(manager.getHistory());

    }
}