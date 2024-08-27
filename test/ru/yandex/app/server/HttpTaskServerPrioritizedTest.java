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

@DisplayName("Тесты для пути /prioritized")
class HttpTaskServerPrioritizedTest extends HttpTaskServerTest {

    @DisplayName("получаем Список приоритетов Задач и проверяем код ответа 200, GET /prioritized")
    @Test
    void shouldReturnStatusCode200AndPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> prioritized = httpTaskServer.taskmanager.getPrioritised();

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> expected = httpTaskServer.gson.fromJson(response.body(), taskType);
        assertEquals(prioritized.size(), expected.size(), "Некорректное количество задач");
    }

    @DisplayName("Некорретный эндпоинт получения Истории задач GET /prioritized/ ")
    @Test
    void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

}