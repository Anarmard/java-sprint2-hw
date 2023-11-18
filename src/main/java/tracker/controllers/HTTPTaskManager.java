package tracker.controllers;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.servers.KVTaskClient;

import java.io.*;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;

    // Также HTTPTaskManager создаёт KVTaskClient, из которого можно получить исходное состояние менеджера.

    public HTTPTaskManager(String url) {
        super(null);
        kvTaskClient = new KVTaskClient(url);
    }

    // Вам нужно заменить вызовы сохранения состояния в файлах на вызов клиента.
    @Override
    public void save() {
        Gson gson = new Gson();
        // запись на сервер по очереди сначала все Task, потом Epic, потом все SubTask
        List<Task> taskList = getAllTasks();
        String jsonTasks = gson.toJson(taskList);
        try {
            kvTaskClient.put("tasks", jsonTasks); // ключ это "tasks", а в json все task сохраняем
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException(e);
        }

        List<Epic> epicList = getAllEpics();
        String jsonEpics = gson.toJson(epicList);
        try {
            kvTaskClient.put("epics", jsonEpics);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException(e);
        }

        List<SubTask> subTaskList = getAllSubTasks();
        String jsonSubTasks = gson.toJson(subTaskList);
        try {
            kvTaskClient.put("subtasks", jsonSubTasks);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException(e);
        }

        // еще надо сохранить историю
        List<Task> historyList = getHistory();
        String jsonHistory = gson.toJson(historyList);
        try {
            kvTaskClient.put("history", jsonHistory);
        } catch (IOException | InterruptedException e) {
            try {
                throw new ManagerSaveException(e);
            } catch (ManagerSaveException ex) {
                ex.printStackTrace();
            }
        }

    }

    public HTTPTaskManager loadFromServer (String url) throws IOException, InterruptedException {
        HTTPTaskManager managerLoadFromServer = new HTTPTaskManager(url);
        Gson gson = new Gson();

        // выгружаем все json из KVServer через клиент kvTaskClient
        // десериализуем в массив Task, Epic, SubTask, Task(для History)
        // затем создаем и вызываем
        String jsonTasks = kvTaskClient.load("tasks");
        Type listTask = new TypeToken<List<Task>>(){}.getType(); // https://stackoverflow.com/questions/5554217/deserialize-a-listt-object-with-gson
        List<Task> taskList = gson.fromJson(jsonTasks, listTask);
        taskList.forEach(managerLoadFromServer::createTask);

        String jsonEpics = kvTaskClient.load("epics");
        Type listEpic = new TypeToken<List<Epic>>(){}.getType();
        List<Epic> epicList = gson.fromJson(jsonEpics, listEpic);
        epicList.forEach(managerLoadFromServer::createEpic);

        String jsonSubTasks = kvTaskClient.load("subtasks");
        Type listSubTasks = new TypeToken<List<SubTask>>(){}.getType();
        List<SubTask> subTaskList = gson.fromJson(jsonSubTasks, listSubTasks);
        subTaskList.forEach(managerLoadFromServer::createSubTask);

        String jsonHistory = kvTaskClient.load("history");
        List<Task> historyList = gson.fromJson(jsonHistory, listTask);
        for (Task task : historyList) {
            managerLoadFromServer.getTaskUser(task.getId());
        }
        return managerLoadFromServer;
    }
}