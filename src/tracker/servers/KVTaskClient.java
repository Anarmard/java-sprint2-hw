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

        try {
            HttpRequest requestForRegistration = HttpRequest.newBuilder() // получаем экземпляр билдера
                    .GET()   // указываем HTTP-метод запроса
                    .uri(URI.create("http://localhost:8078/register")) // указываем адрес сервера
                    .build(); // создаём ("строим") http-запрос
            HttpResponse<String> response = client.send(requestForRegistration, handler);
            this.apiToken = response.body();
            int status = response.statusCode();
            // обрабатываем коды успешного состояния
            if(status == 200) {
                System.out.println("Сервер успешно обработал запрос. Код состояния: " + status);
            } else {
                System.out.println("Сервер сообщил о проблеме с запросом. Код состояния: " + status);
            }
        } catch (IOException | InterruptedException e) {
            //throw new ManagerSaveException(e);
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
        int status = response.statusCode();
        // обрабатываем коды успешного состояния
        if(status == 200) {
            System.out.println("Сервер успешно обработал запрос. Код состояния: " + status);
        } else {
            System.out.println("Сервер сообщил о проблеме с запросом. Код состояния: " + status);
        }

    }

    //Метод String load(String key) должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
    public String load(String key) throws IOException, InterruptedException {
        // создаём экземпляр URI, содержащий адрес нужного ресурса
        try {
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
            int status = response.statusCode();
            // обрабатываем коды успешного состояния
            if(status == 200) {
                System.out.println("Сервер успешно обработал запрос. Код состояния: " + status);
            } else {
                System.out.println("Сервер сообщил о проблеме с запросом. Код состояния: " + status);
            }
            return String.valueOf(response);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Не удалось сохранить значение: ", e.getMessage());
        }
    }
}
