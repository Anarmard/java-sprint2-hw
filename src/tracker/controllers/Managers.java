package tracker.controllers;

public class Managers {

    public static TaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8080/");
        //return new FileBackedTasksManager("TasksCSV.csv");
        //return new InMemoryTaskManager();
    }

    // В конце обновите статический метод getDefault() в утилитарном классе Managers, чтобы он возвращал HTTPTaskManager.

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
