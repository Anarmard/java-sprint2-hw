package tracker.controllers;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{
    public FileBackedTasksManagerTest() {
        super(new FileBackedTasksManager("TasksCSV.csv"));
    }
}