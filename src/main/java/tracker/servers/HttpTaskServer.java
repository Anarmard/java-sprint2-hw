package tracker.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.source.util.SourcePositions;
import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static TaskManager taskManager;
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();

        // IOException могут сгенерировать методы create() и bind(...)
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/subtask", new SubTaskHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.createContext("/tasks", new AllTaskHandler()); // получение всех задач getPrioritizedTasks

        httpServer.start(); // запускаем сервер
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod(); // возвращает HTTP-метод, который клиент использовал при отправке запроса
            System.out.println("Началась обработка метода: " + method);
            switch(method) { // логика сервера, что ответить пользователю
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody(); // Метод возвращает тело запроса, то есть данные, которые клиент отправил на сервер
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET); // считать из него массив байтов, сконвертировать в строковый тип

                    Gson gson = new Gson();
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getId() == 0) {
                        taskManager.createTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Task добавлен".getBytes());
                        }
                    } else {
                        taskManager.updateTask(task);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Task обновлен".getBytes());
                        }
                    }
                    break;
                case "GET":
                    String path = httpExchange.getRequestURI().getPath();
                    String idString = path.split("/")[3];

                    if (idString != null) {
                        System.out.println(idString);
                        System.out.println(taskManager.getAllTasks());
                        System.out.println(taskManager.getTaskByID(Long.valueOf(idString)));
                        if (taskManager.getTaskByID(Long.valueOf(idString)) == null) {
                            System.out.println("Задачи с таким ID не найдено");
                            httpExchange.sendResponseHeaders(404, 0);
                            return;
                        }
                        System.out.println(taskManager.getTaskUser(Long.valueOf(idString)));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Task получен".getBytes());
                        }
                    } else {
                        System.out.println(taskManager.getAllTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Все Task получены".getBytes());
                        }
                    }
                    break;
                case "DELETE":
                    path = httpExchange.getRequestURI().getPath();
                    idString = path.split("/")[3];

                    if (idString != null) {
                        if (taskManager.getTaskByID(Long.valueOf(idString)) == null) {
                            System.out.println("Задачи с таким ID не найдено");
                            httpExchange.sendResponseHeaders(404, 0);
                            return;
                        }
                        taskManager.deleteTaskByID(Long.valueOf(idString));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Task удален".getBytes());
                        }
                    } else {
                        taskManager.deleteAllTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Все Task удалены".getBytes());
                        }
                    }
                    break;
                default:
                    String response = "Вы использовали какой-то другой метод!";
            }

        }
    }

    static class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка метода: " + method);

            String response;
            switch(method) { // логика сервера, что ответить пользователю
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody(); // Метод возвращает тело запроса, то есть данные, которые клиент отправил на сервер
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET); // считать из него массив байтов, сконвертировать в строковый тип

                    Gson gson = new Gson();
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getId() == 0) {
                        taskManager.createEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Epic добавлен".getBytes());
                        }
                    } else {
                        taskManager.updateEpic(epic);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Epic обновлен".getBytes());
                        }
                    }
                    break;
                case "GET":
                    String path = httpExchange.getRequestURI().getPath();
                    String idString = path.split("/")[3];

                    if (idString != null) {
                        System.out.println(taskManager.getEpicUser(Long.valueOf(idString)));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Epic получен".getBytes());
                        }
                    } else {
                        System.out.println(taskManager.getAllEpics());
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Все Epic получены".getBytes());
                        }
                    }
                    break;
                case "DELETE":
                    path = httpExchange.getRequestURI().getPath();
                    idString = path.split("/")[3];

                    if (idString != null) {
                        taskManager.deleteEpicByID(Long.valueOf(idString));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Epic удален".getBytes());
                        }
                    } else {
                        taskManager.deleteAllEpics();
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Все Epic удалены".getBytes());
                        }
                    }
                    break;
                default:
                    response = "Вы использовали какой-то другой метод!";
            }
        }
    }

    static class SubTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка метода: " + method);

            switch(method) { // логика сервера, что ответить пользователю
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody(); // Метод возвращает тело запроса, то есть данные, которые клиент отправил на сервер
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET); // считать из него массив байтов, сконвертировать в строковый тип

                    Gson gson = new Gson();
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    if (subTask.getId() == 0) {
                        taskManager.createSubTask(subTask);
                    } else {
                        taskManager.updateSubTask(subTask);
                    }

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Задача добавлена".getBytes());
                    }
                    break;
                case "GET":
                    String path = httpExchange.getRequestURI().getPath();
                    String idString = path.split("/")[3];

                    if (idString == null) {
                        System.out.println(taskManager.getAllSubTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Все SubTask получены".getBytes());
                        }
                    } else if (idString.equals("epic")) {
                        String idEpicString = path.split("/")[4];
                        System.out.println(taskManager.getSubTasksByEpicId(Long.valueOf(idEpicString)));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Все SubTask Эпика получены".getBytes());
                        }
                    } else {
                        System.out.println(taskManager.getSubTaskUser(Long.valueOf(idString)));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("SubTask получен".getBytes());
                        }
                    }
                    break;
                case "DELETE":
                    path = httpExchange.getRequestURI().getPath();
                    idString = path.split("/")[3];

                    if (idString != null) {
                        taskManager.deleteSubTaskByID(Long.valueOf(idString));
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("SubTask удален".getBytes());
                        }
                    } else {
                        taskManager.deleteAllSubTasks();
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write("Все SubTask удалены".getBytes());
                        }
                    }
                    break;
                default:
                    System.out.println("Вы использовали какой-то другой метод!");
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка метода: " + method);
            switch(method) { // логика сервера, что ответить пользователю
                case "GET":
                    System.out.println(taskManager.getHistory());
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("История просмотров получена".getBytes());
                    }
                    break;
                default:
                    String response = "Для historyManager используется только GET";
            }
        }
    }

    static class AllTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            System.out.println("Началась обработка метода: " + method);

            switch(method) { // логика сервера, что ответить пользователю
                case "GET":
                    System.out.println(taskManager.getPrioritizedTasks());
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write("Получили отсортированный список задач".getBytes());
                    }
                    break;
                default:
                    String response = "Для отсортированного списка используется только GET";
            }
        }
    }

}