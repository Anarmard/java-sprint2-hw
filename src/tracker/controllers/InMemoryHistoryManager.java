package tracker.controllers;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public List<Task> historyTaskList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyTaskList.size() >= 10) {
            historyTaskList.remove(0);
        }
        historyTaskList.add(task);
    }

    @Override
    public List<Task> getHistory(){
        return historyTaskList;
    }
}
