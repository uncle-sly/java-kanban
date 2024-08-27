package ru.yandex.app.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.app.converter.DurationTypeAdapter;
import ru.yandex.app.converter.LocalDateTimeTypeAdapter;
import ru.yandex.app.handlers.*;
import ru.yandex.app.service.Managers;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServer {

    public static final int PORT = 8080;
    TaskManager taskmanager;
    HttpServer server;
    ErrorHandler errorHandler;
    Gson gson;

    public HttpTaskServer() {
        this(Managers.getInMemoryTaskManager());
    }

    public HttpTaskServer(TaskManager manager) {
        this.taskmanager = manager;
        this.gson = getGson();
        this.errorHandler = new ErrorHandler(gson);
        try {
            this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        переделать с лямбд :: на классы соответствующих обработчиков
        server.createContext("/tasks", new TaskHandler(taskmanager, gson, errorHandler));
        server.createContext("/subtasks", new SubTaskHandler(taskmanager, gson, errorHandler));
        server.createContext("/epics", new EpicHandler(taskmanager, gson, errorHandler));
        server.createContext("/history", new HistoryHandler(taskmanager, gson, errorHandler));
        server.createContext("/prioritized", new PrioritizedHandler(taskmanager, gson, errorHandler));

    }


    public void start() {
        server.start();
        System.out.println("HttpServer запущен на " + PORT + " порту.");

    }

    public void stop() {
        server.stop(0);
        System.out.println("HttpServer остановлен на " + PORT + " порту.");
    }

    static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.setPrettyPrinting();

        return gsonBuilder.create();
    }

    public TaskManager getTaskmanager() {
        return taskmanager;
    }
}
