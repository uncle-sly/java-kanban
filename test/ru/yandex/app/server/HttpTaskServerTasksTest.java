package ru.yandex.app.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Status;
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
import static ru.yandex.app.handlers.BaseHttpHandler.gson;

@DisplayName("Тесты для пути /tasks")
class HttpTaskServerTasksTest extends HttpTaskServerTest {

    @DisplayName("получаем список задач и проверяем код ответа 200, GET /tasks")
    @Test
    void shouldReturnStatusCode200AndCorrectNumberOfTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = httpTaskServer.getTaskmanager().getAllTasks();

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> expected = gson.fromJson(response.body(), taskType);
        assertNotNull(expected, "Список задач не вернулся");
        assertEquals(expected.size(), tasks.size(), "Некорректное количество задач");
        assertEquals(tasks.get(0), expected.get(0), "Задачи не сопадают");
    }

    @DisplayName("Некорретный эндпоинт получения списка задач GET /tasks/ ")
    @Test
    void shouldReturnNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

    @DisplayName("получаем задачу и проверяем код ответа 200, GET /tasks/id")
    @Test
    void shouldReturnStatusCode200AndRequestedTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task requestedTask = httpTaskServer.getTaskmanager().getTaskById(2);

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task expectedTask = gson.fromJson(response.body(), taskType);
        assertNotNull(expectedTask, "задача не вернулась");
        assertEquals(requestedTask.getUid(), expectedTask.getUid(), "ID задач не сопадает");
    }

    @DisplayName("Некорретный ID задачи, получаем код ответа 404, GET /tasks/id")
    @Test
    void shouldReturnNotFoundAndStatus404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/5");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("создаем новую задачу и проверяем код ответа 201. POST /tasks")
    @Test
    void shouldReturnStatusCode201AndCreatedTask() throws IOException, InterruptedException {
        Task newTask3 = new Task("New Task 3", "", Status.NEW, Duration.of(10, ChronoUnit.MINUTES),
                LocalDateTime.parse("2020-01-03T10:00"));
        String jsonTask = gson.toJson(newTask3);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        String expected = gson.fromJson(response.body(), String.class);
        assertEquals("Новая задача создана.", expected, "задача не создана");
    }

    @DisplayName("обновляем существующую задачу и проверяем код ответа 201. POST /tasks")
    @Test
    void shouldReturnStatusCode201AndUpdatedTask() throws IOException, InterruptedException {
        Task updateTask2 = new Task(2, "Task 2", "", Status.DONE, Duration.of(10, ChronoUnit.MINUTES),
                LocalDateTime.parse("2020-01-03T10:00"));

        String jsonTask = gson.toJson(updateTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasks = httpTaskServer.getTaskmanager().getAllTasks();
        String expected = gson.fromJson(response.body(), String.class);
        assertEquals("Задача обновлена.", expected, "Задача не обновлена.");
        assertEquals(tasks.get(updateTask2.getUid() - 1).getStatus(), updateTask2.getStatus(), "Статус задачи не сопадает");
    }


    @DisplayName("удаляем задачу и проверяем код ответа 204, DELETE /tasks/id")
    @Test
    void shouldReturnStatusCode204AndDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(204, response.statusCode());

        List<Task> tasks = httpTaskServer.getTaskmanager().getAllTasks();
        assertFalse(tasks.contains(httpTaskServer.getTaskmanager().getTaskById(2)), "задача не удалена");
    }

    @DisplayName("Проверяем пересечения: с началом другой задачи.")
    @Test
    void shouldReturnStatusCode406BecauseOfIntersectionWithBeginning() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");

//        пересекается с началом другой задачи
        Task newTask4 = new Task("Http Task 4", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-09-01T12:45"));
        String jsonTask = gson.toJson(newTask4);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        String expected = gson.fromJson(response.body(), String.class);
        assertEquals("Есть пересечение с другими задачами.", expected);
    }


    @DisplayName("Проверяем пересечения: с окончанием другой задачи.")
    @Test
    void shouldReturnStatusCode406BecauseOfIntersectionWithEnd() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");

//        пересекается с окончанием другой задачи
        Task newTask4 = new Task("Http Task 5", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-09-01T14:25"));
        String jsonTask = gson.toJson(newTask4);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        String expected = gson.fromJson(response.body(), String.class);
        assertEquals("Есть пересечение с другими задачами.", expected);
    }


    @DisplayName("Проверяем пересечения: попадает внутрь интервала другой задачи.")
    @Test
    void shouldReturnStatusCode406BecauseOfIntersectionInside() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");

//        попадает внутрь другой задачи
        Task newTask4 = new Task("Http Task 6", "", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-09-01T13:10"));
        String jsonTask = gson.toJson(newTask4);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        String expected = gson.fromJson(response.body(), String.class);
        assertEquals("Есть пересечение с другими задачами.", expected);
    }

}