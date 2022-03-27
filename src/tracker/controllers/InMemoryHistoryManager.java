package tracker.controllers;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    public List<Task> historyTaskList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyTaskList.size() >= 10) {
                historyTaskList.remove(0);
            }
            historyTaskList.add(task);
        }
    }

    @Override
    public void remove(int id) {
        historyTaskList.remove(id);
    }

    @Override
    public List<Task> getHistory(){
        return historyTaskList;
    }
}
