package tracker.controllers;

import tracker.exceptions.ManagerSaveException;

public class Managers {

    public static TaskManager getDefault() throws ManagerSaveException {
        return new HTTPTaskManager("http://localhost:8078/");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
