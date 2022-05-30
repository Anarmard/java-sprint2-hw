import org.junit.jupiter.api.Test;

import tracker.controllers.*;
import tracker.model.Task;
import tracker.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager = Managers.getDefaultHistory();
    List<Task> history = historyManager.getHistory();

    private static final Task task1 = new Task("1Task", "1TaskDescription", 1L, TaskStatus.NEW);
    private static final Task task2 = new Task("2Task", "2TaskDescription", 2L, TaskStatus.NEW);
    private static final Task task3 = new Task("3Task", "3TaskDescription", 3L, TaskStatus.NEW);
    private static final Task task4 = new Task("4Task", "4TaskDescription", 4L, TaskStatus.NEW);

    @Test
    void emptyHistory() { // пустая история задач
        List<Task> blankList = new ArrayList<>();
        assertEquals(blankList, history, "История не пустая.");
    }

    @Test
    void addTaskInHistory() { // добавление одной таски
        historyManager.add(task1);
        history = historyManager.getHistory();
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "Размер истории не 1.");
    }

    @Test
    void addRepeatedTask() { // добавление 2 раза одной таски
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task2); // дублирование
        history = historyManager.getHistory();
        assertEquals(4, history.size(), "Размер истории не 4.");
    }

    @Test
    void deleteTaskFromHistoryMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task2);
        historyManager.remove(3L); //удаление: середина
        history = historyManager.getHistory();
        assertEquals(3, history.size(), "Размер истории не 3.");
    }

    @Test
    void deleteTaskFromHistoryEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task2);
        historyManager.remove(3L); //удаление: середина
        historyManager.remove(2L); //удаление: конец
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "Размер истории не 2.");
    }

    @Test
    void deleteTaskFromHistoryBegin() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task2);
        historyManager.remove(3L); //удаление: середина
        historyManager.remove(2L); //удаление: конец
        historyManager.remove(1L); //удаление: начало
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "Размер истории не 1.");
        assertEquals("4Task", history.get(0).getName(), "Осталась не 4Task");
    }
}