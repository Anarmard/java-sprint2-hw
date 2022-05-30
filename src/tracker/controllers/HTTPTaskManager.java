package tracker.controllers;

import com.google.gson.Gson;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskType;
import tracker.servers.KVTaskClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;

    // Также HTTPTaskManager создаёт KVTaskClient, из которого можно получить исходное состояние менеджера.

    public HTTPTaskManager(String url) {
        super(null);
        kvTaskClient = new KVTaskClient(url);
    }

    // Вам нужно заменить вызовы сохранения состояния в файлах на вызов клиента.
    @Override
    public void save() {
        // запись на сервер по очереди сначала все Task, потом Epic, потом все SubTask
        List<Task> taskList = getAllTasks();
        for (Task task : taskList) {
            Gson gson = new Gson();
            String json = gson.toJson(task);
            kvTaskClient.put(String.valueOf(task.getId()), json);
        }

        List<Epic> epicList = getAllEpics();
        for (Epic epic : epicList) {
            Gson gson = new Gson();
            String json = gson.toJson(epic);
            kvTaskClient.put(String.valueOf(epic.getId()), json);
        }

        List<SubTask> subTaskList = getAllSubTasks();
        for (SubTask subTask : subTaskList) {
            Gson gson = new Gson();
            String json = gson.toJson(subTask);
            kvTaskClient.put(String.valueOf(subTask.getId()), json);
        }
    }
}