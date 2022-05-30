package tracker.servers;

/*
Для работы с хранилищем вам потребуется HTTP-клиент, который будет делегировать вызовы методов в HTTP-запросы.
Создайте класс KVTaskClient. Его будет использовать класс HTTPTaskManager, который мы скоро напишем.
 */

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;

    //При создании KVTaskClient учтите следующее:
    //Конструктор принимает URL к серверу хранилища и регистрируется. При регистрации выдаётся токен (API_TOKEN), который нужен при работе с сервером.
    public KVTaskClient(String url) {
        this.url = url;
    }

    //Метод void put(String key, String json) должен сохранять состояние менеджера задач через запрос POST /save/<ключ>?API_TOKEN=
    public void put(String key, String json) {
        // создаём экземпляр URI, содержащий адрес нужного ресурса
        URI uri = URI.create("http://localhost:8078/" + "save/" + key + "?API_TOKEN=" + "DEBUG");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .POST(body)    // указываем HTTP-метод запроса
                .uri(uri) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .header("Accept", "application/json") // указываем заголовок Accept
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос

        // HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();

        // получаем стандартный обработчик тела запроса с конвертацией содержимого в строку
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        // отправляем запрос и получаем ответ от сервера
        try {
            HttpResponse<String> response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    //Метод String load(String key) должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=
    public String load(String key) throws IOException, InterruptedException {
        // создаём экземпляр URI, содержащий адрес нужного ресурса
        URI uri = URI.create("http://localhost:8078/" + "load/" + key + "?API_TOKEN=" + "DEBUG");

        // создайте объект, описывающий HTTP-запрос
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        // создайте HTTP-клиент с настройками по умолчанию
        HttpClient client = HttpClient.newHttpClient();

        // получите стандартный обработчик тела запроса
        // с конвертацией содержимого в строку
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            // отправьте запрос
            HttpResponse<String> response = client.send(request, handler);
            return String.valueOf(response);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            return "";
        }
    }

    //Далее проверьте код клиента в main. Для этого запустите KVServer, создайте экземпляр KVTaskClient.
    // Затем сохраните значение под разными ключами и проверьте, что при запросе возвращаются нужные данные.
    // Удостоверьтесь, что если изменить значение, то при повторном вызове вернётся уже не старое, а новое.

}
