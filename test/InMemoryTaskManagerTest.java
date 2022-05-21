import tracker.controllers.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }
}