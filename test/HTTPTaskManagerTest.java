import tracker.controllers.FileBackedTasksManager;
import tracker.controllers.HTTPTaskManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.servers.HttpTaskServer;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    public HTTPTaskManagerTest() {
        super(new HTTPTaskManager("http://localhost:8080/"));
    }

    private final HTTPTaskManager httpTaskManager = new HTTPTaskManager("http://localhost:8080/tasks/task/");

    /*
    //==================================== Как создать задачу ==============================================
    // создаём экземпляр URI, содержащий адрес нужного ресурса
    URI url = URI.create("http://localhost:8080/tasks/task/");

        Gson gson = new Gson();
        String json = gson.toJson(task1); // сериализуйте объект класса Task в JSON

        // создаём объект, описывающий HTTP-запрос
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        // HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();

        // отправляем запрос и получаем ответ от сервера
        // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //==================================== Как получить задачу с id = 1 ==============================================
        HttpClient client1 = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response1 = client1.send(request1, HttpResponse.BodyHandlers.ofString());

        //==================================== Как получить все задачи==============================================
        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());
                         */

}