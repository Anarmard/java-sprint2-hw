import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.HTTPTaskManager;
import tracker.servers.KVServer;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.servers.HttpTaskServer;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    public HTTPTaskManagerTest() {
        super(new HTTPTaskManager("http://localhost:8078/"));
    }

    private final HTTPTaskManager httpTaskManager = new HTTPTaskManager("http://localhost:8078/");

}