package ru.yandex.app.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.app.model.Epic;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@DisplayName("Тесты для HttpTaskServer")
class HttpTaskServerTest {
    HttpTaskServer httpTaskServer;

    @BeforeEach
    void init() {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        Task task1 = httpTaskServer.getTaskmanager().createTask(new Task("New Task 1", "", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00")));
        Task task2 = httpTaskServer.getTaskmanager().createTask(new Task("New Task 2", "", Status.NEW, Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-02-01T10:00")));
        httpTaskServer.getTaskmanager().getTaskById(task2.getUid());
        Epic epic1 = httpTaskServer.getTaskmanager().createEpic(new Epic("New Epic 1"));
        Epic epic2 = httpTaskServer.getTaskmanager().createEpic(new Epic("New Epic 2"));
        SubTask subTask1 = httpTaskServer.getTaskmanager().createSubTask(new SubTask("New SubTask 1", "department 1", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T15:00"), epic1.getUid()));
        SubTask subTask2 = httpTaskServer.getTaskmanager().createSubTask(new SubTask("New SubTask 2", "department 2", Status.NEW, Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-02-01T15:00"), epic1.getUid()));

    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
    }

}
