package ru.yandex.app.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.app.handlers.*;
import ru.yandex.app.service.Managers;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static final int PORT = 8080;
    TaskManager taskmanager;
    HttpServer server;
    ErrorHandler errorHandler;

    public HttpTaskServer() {
        this(Managers.getInMemoryTaskManager());
    }

    public HttpTaskServer(TaskManager manager) {
        this.taskmanager = manager;
        this.errorHandler = new ErrorHandler();
        try {
            this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        переделать с лямбд :: на классы соответствующих обработчиков
        server.createContext("/tasks", new TaskHandler(taskmanager, errorHandler));
        server.createContext("/subtasks", new SubTaskHandler(taskmanager, errorHandler));
        server.createContext("/epics", new EpicHandler(taskmanager, errorHandler));
        server.createContext("/history", new HistoryHandler(taskmanager, errorHandler));
        server.createContext("/prioritized", new PrioritizedHandler(taskmanager, errorHandler));

    }

    public void start() {
        server.start();
        System.out.println("HttpServer запущен на " + PORT + " порту.");

    }

    public void stop() {
        server.stop(0);
        System.out.println("HttpServer остановлен на " + PORT + " порту.");
    }

    public TaskManager getTaskmanager() {
        return taskmanager;
    }
}
