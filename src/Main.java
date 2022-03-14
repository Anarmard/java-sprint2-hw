import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        // создание 2-х задач
        Task task1 = new Task("Продукты", "Заехать в магазин", 0L, "NEW");
        manager.createTask(task1);
        Task task2 = new Task("Ремонт", "Покрасить стену", 0L, "NEW");
        manager.createTask(task2);

        // создание Эпика с 2-мя подзадачами
        Epic epic1 = new Epic ("Уборка дома", "Еженедельная процедура", 0L, "NEW", new ArrayList<>());
        manager.createEpic(epic1);
        SubTask subTask11 = new SubTask("Полы", "Помыть полы", 0L, "NEW", 3L);
        manager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("Стирка", "Постирать вещи", 0L, "NEW", 3L);
        manager.createSubTask(subTask12);

        // создание Эпика с 1-й подзадачой
        Epic epic2 = new Epic ("Спорт", "Улучшить здоровье", 0L, "NEW", new ArrayList<>());
        manager.createEpic(epic2);
        SubTask subTask21 = new SubTask("Гимнастика", "Утренняя гимнастика", 0L, "NEW", 6L);
        manager.createSubTask(subTask21);

        // печать всех типов задач
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        // Изменение статуса у задачи task1 и подзадачи subTask11
        task1.status = "IN_PROGRESS";
        manager.updateTask(task1);
        subTask11.status = "DONE";
        manager.updateSubTask(subTask11);
        System.out.println("Печать всех задач/эпиков/подзадач после их изменения:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics()); // тут важно проверить, что статус у эпика поменялся - ок
        System.out.println(manager.getAllSubTasks());

        //Удаление задачи task2, эпика epic2, и подзадачи subTask12
        manager.deleteTaskByID(2L);
        manager.deleteEpicByID(6L);
        manager.deleteSubTaskByID(5L);
        System.out.println("Печать всех задач/эпиков/подзадач после удалений:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics()); // тут важно проверить, что статус у эпика поменялся - ок
        System.out.println(manager.getAllSubTasks());

        System.out.println(manager.getSubTasksByEpicId(3L));
    }
}