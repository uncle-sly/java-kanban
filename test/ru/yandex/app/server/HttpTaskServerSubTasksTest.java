package ru.yandex.app.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для пути /subtasks")
class HttpTaskServerSubTasksTest extends HttpTaskServerTest {

    @DisplayName("получаем список подзадач и проверяем код ответа 200, GET /subtasks")
    @Test
    void shouldReturnStatusCode200AndCorrectNumberOfSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<SubTask> subTasks = httpTaskServer.taskmanager.getAllSubTasks();
        Type taskType = new TypeToken<ArrayList<SubTask>>() {
        }.getType();
        List<SubTask> expectedSubTask = httpTaskServer.gson.fromJson(response.body(), taskType);

        assertNotNull(expectedSubTask, "Список подзадач не вернулся");
        assertEquals(expectedSubTask.size(), subTasks.size(), "Некорректное количество подзадач");
        assertEquals(subTasks.get(0), expectedSubTask.get(0), "Подзадачи не сопадают");
    }

    @DisplayName("Некорретный эндпоинт получения списка подзадач GET /subtasks/ ")
    @Test
    void shouldReturnNotFoundAndStatusCode500() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

    @DisplayName("получаем подзадачу и проверяем код ответа 200, GET /subtasks/id")
    @Test
    void shouldReturnStatusCode200AndRequestedSubTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        SubTask requestedSubTask = httpTaskServer.taskmanager.getSubTaskById(5);

        Type taskType = new TypeToken<SubTask>() {
        }.getType();
        SubTask expectedSubTask = httpTaskServer.gson.fromJson(response.body(), taskType);
        assertNotNull(expectedSubTask, "подзадача не вернулась");
        assertEquals(requestedSubTask.getUid(), expectedSubTask.getUid(), "ID подзадачи не сопадает");
    }

    @DisplayName("Некорретный ID подзадачи, получаем код ответа 404, GET /subtasks/id")
    @Test
    void shouldReturnNotFoundAndStatus404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/7");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("создаем новую подзадачу и проверяем код ответа 201. POST /subtasks")
    @Test
    void shouldReturnStatusCode201AndCreatedTask() throws IOException, InterruptedException {

        SubTask newSubTask3 = new SubTask("New SubTask 3", "department 3", Status.NEW, Duration.of(50, ChronoUnit.MINUTES),
                LocalDateTime.parse("2020-02-02T15:00"), 3);


        String jsonTask = httpTaskServer.gson.toJson(newSubTask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        String expected = httpTaskServer.gson.fromJson(response.body(), String.class);
        assertEquals("Новая подзадача создана.", expected, "подзадача не создана");
    }

    @DisplayName("обновляем существующую подзадачу и проверяем код ответа 201. POST /subtasks")
    @Test
    void shouldReturnStatusCode201AndUpdatedSubTask() throws IOException, InterruptedException {
        Task updateSubTask2 = new SubTask(6, "SubTask 2", "department 3", Status.DONE, Duration.of(50, ChronoUnit.MINUTES),
                LocalDateTime.parse("2020-02-02T15:00"), 3);

        String jsonTask = httpTaskServer.gson.toJson(updateSubTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> subTasks = httpTaskServer.taskmanager.getAllSubTasks();
        String expected = httpTaskServer.gson.fromJson(response.body(), String.class);

        assertEquals("Подзадача обновлена.", expected, "Подзадача не обновлена.");
        assertEquals(subTasks.get(1).getStatus(), updateSubTask2.getStatus(), "Статус задачи не сопадает");
    }


    @DisplayName("удаляем подзадачу и проверяем код ответа 204, DELETE /subtasks/id")
    @Test
    void shouldReturnStatusCode204AndDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/5");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<SubTask> subTasks = httpTaskServer.taskmanager.getAllSubTasks();
        assertFalse(subTasks.contains(httpTaskServer.taskmanager.getSubTaskById(5)), "задача не удалена");
    }

}