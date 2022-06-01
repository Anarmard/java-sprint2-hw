package tracker.servers;

import tracker.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String urlKVServer;
    private final HttpClient client = HttpClient.newHttpClient(); // HTTP-клиент с настройками по умолчанию
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(); // стандартный обработчик тела запроса с конвертацией содержимого в строку
    private String apiToken;

    //При создании KVTaskClient учтите следующее:
    //Конструктор принимает URL к серверу хранилища и регистрируется. При регистрации выдаётся токен (API_TOKEN), который нужен при работе с сервером.
    public KVTaskClient(String urlKVServer) {
        this.urlKVServer = urlKVServer;

        // Александр, надеюсь я правильно понял ваш комментарий (УДАЛИТЬ)
        try {
            HttpRequest requestForRegistration = HttpRequest.newBuilder() // получаем экземпляр билдера
                    .GET()   // указываем HTTP-метод запроса
                    .uri(URI.create("http://localhost:8078/register")) // указываем адрес сервера
                    .build(); // создаём ("строим") http-запрос
            this.apiToken = client.send(requestForRegistration, handler).body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + urlKVServer + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    //Метод void put(String key, String json) должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
    public void put(String key, String json) throws IOException, InterruptedException {
        // создаём экземпляр URI, содержащий адрес нужного ресурса
        URI uri = URI.create("http://localhost:8078/" + "save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .POST(body)    // указываем HTTP-метод запроса
                .uri(uri) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .header("Accept", "application/json") // указываем заголовок Accept
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос

        // отправляем запрос и получаем ответ от сервера
        HttpResponse<String> response = client.send(request, handler);
    }

    //Метод String load(String key) должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
    public String load(String key) throws IOException, InterruptedException {
        // создаём экземпляр URI, содержащий адрес нужного ресурса
        URI uri = URI.create("http://localhost:8078/" + "load/" + key + "?API_TOKEN=" + apiToken);

        // создайте объект, описывающий HTTP-запрос
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        // отправьте запрос
        HttpResponse<String> response = client.send(request, handler);
        return String.valueOf(response); // Александр, я верно понял ваш комментарий про пробрасывание? (УДАЛИТЬ)
    }
}
