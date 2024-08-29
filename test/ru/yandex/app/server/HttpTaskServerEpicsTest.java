package ru.yandex.app.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Epic;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.app.handlers.BaseHttpHandler.gson;

@DisplayName("Тесты для пути /epics")
class HttpTaskServerEpicsTest extends HttpTaskServerTest {

    @DisplayName("получаем список Эпиков и проверяем код ответа 200, GET /epics")
    @Test
    void shouldReturnStatusCode200AndCorrectNumberOfEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epics = httpTaskServer.getTaskmanager().getAllEpics();

        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> expectedEpics = gson.fromJson(response.body(), taskType);
        assertNotNull(expectedEpics, "Список Эпиков не вернулся");
        assertEquals(expectedEpics.size(), epics.size(), "Некорректное количество задач");
        assertEquals(epics.get(0), expectedEpics.get(0), "Задачи не сопадают");
    }

    @DisplayName("Некорретный эндпоинт получения списка Эпиков GET /epics/ ")
    @Test
    void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("получаем Эпик и проверяем код ответа 200, GET /epics/id")
    @Test
    void shouldReturnStatusCode200AndRequestedEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/4");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic requestedEpic = httpTaskServer.getTaskmanager().getEpicById(4);

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic expectedEpic = gson.fromJson(response.body(), taskType);
        assertNotNull(expectedEpic, "Эпик не вернулся");
        assertEquals(requestedEpic.getUid(), expectedEpic.getUid(), "ID Эпика не сопадает");
    }

    @DisplayName("Некорретный ID Эпика, получаем код ответа 404, GET /epics/id")
    @Test
    void shouldReturnNotFoundAndStatus404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/5");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("создаем новый Эпик и проверяем код ответа 201. POST /epics")
    @Test
    void shouldReturnStatusCode201AndCreatedEpic() throws IOException, InterruptedException {
        Epic newEpic3 = new Epic("New Epic 3");
        String jsonTask = gson.toJson(newEpic3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        String expected = gson.fromJson(response.body(), String.class);
        assertEquals("Новый Эпик создан.", expected, "Эпик не создан");
    }

    @DisplayName("обновляем существующий Эпик и проверяем код ответа 201. POST /epics")
    @Test
    void shouldReturnStatusCode201AndUpdatedTask() throws IOException, InterruptedException {
        Epic updateEpic2 = new Epic(4, "Old Epic 2");
        String jsonTask = gson.toJson(updateEpic2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epics = httpTaskServer.getTaskmanager().getAllEpics();
        String expected = gson.fromJson(response.body(), String.class);

        assertEquals("Эпик обновлен.", expected, "Эпик не обновлен.");
        assertEquals(epics.get(1).getName(), updateEpic2.getName(), "Имя Эпика не сопадает");
    }

    @DisplayName("удаляем Эпик и проверяем код ответа 204, DELETE /epics/id")
    @Test
    void shouldReturnStatusCode204AndDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/4");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<Epic> epics = httpTaskServer.getTaskmanager().getAllEpics();
        assertFalse(epics.contains(httpTaskServer.getTaskmanager().getEpicById(4)), "задача не удалена");
    }

}