package ru.yandex.app.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.app.handlers.BaseHttpHandler.gson;

@DisplayName("Тесты для пути /history")
class HttpTaskServerHistoryTest extends HttpTaskServerTest {

    @DisplayName("получаем Историю Задач и проверяем код ответа 200, GET /history")
    @Test
    void shouldReturnStatusCode200AndTasksHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> history = httpTaskServer.getTaskmanager().getHistory();

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> expectedHistory = gson.fromJson(response.body(), taskType);
        assertEquals(history.size(), expectedHistory.size(), "Некорректное количество задач");
    }

    @DisplayName("Некорретный эндпоинт получения Истории задач GET /history/ ")
    @Test
    void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

}