package ru.yandex.app;

import ru.yandex.app.model.Epic;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.server.HttpTaskServer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) {

//        настройка и запуск HTTP-сервера
        HttpTaskServer httpTaskServer = new HttpTaskServer();
//        запускаем сервер
        httpTaskServer.start();

        Epic epic1 = httpTaskServer.getTaskmanager().createEpic(new Epic("New Epic 1"));
        System.out.println(epic1);
        Epic getEpic = httpTaskServer.getTaskmanager().getEpicById(epic1.getUid());
        SubTask subTask1 = httpTaskServer.getTaskmanager().createSubTask(new SubTask("New SubTask 1", "department 1", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T15:00"), epic1.getUid()));
        System.out.println("\nCreated SubTask: " + subTask1);
        SubTask subTask2 = httpTaskServer.getTaskmanager().createSubTask(new SubTask("New SubTask 2", "department 2", Status.NEW, Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-02-01T15:00"), epic1.getUid()));
        System.out.println("Created SubTask: " + subTask2);
        SubTask getSub1 = httpTaskServer.getTaskmanager().getSubTaskById(subTask1.getUid());

//      ---Tasks---
        Task task1 = httpTaskServer.getTaskmanager().createTask(new Task("New Task 1", "", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00")));
        System.out.println("Created task: " + task1);
        Task task2 = httpTaskServer.getTaskmanager().createTask(new Task("New Task 2", "", Status.NEW, Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-02-01T10:00")));
        System.out.println("Created task: " + task2);
        Task task3 = httpTaskServer.getTaskmanager().createTask(new Task("New Task 3", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2020-03-01T10:00")));
        Task taskFromManager3 = httpTaskServer.getTaskmanager().getTaskById(task3.getUid());
        Task task4 = httpTaskServer.getTaskmanager().createTask(new Task("New Task 4", "", Status.NEW, Duration.of(40, ChronoUnit.MINUTES), LocalDateTime.parse("2020-04-01T10:00")));

    }
}
