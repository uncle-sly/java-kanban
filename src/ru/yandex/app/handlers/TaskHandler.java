package ru.yandex.app.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.app.exceptions.ValidationException;
import ru.yandex.app.model.Task;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;
    Gson gson;
    ErrorHandler errorHandler;

    public TaskHandler(TaskManager taskManager, Gson gson, ErrorHandler errorHandler) {
        this.taskManager = taskManager;
        this.gson = gson;
        this.errorHandler = errorHandler;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            int id = 0;
            String response;

            switch (requestMethod) {

                case "GET": {
                    if (Pattern.matches("^/tasks$", path)) {
                        response = gson.toJson(taskManager.getAllTasks());
                        sendText(httpExchange, 200, response);
                    } else if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String[] pathId = path.split("/");
                        id = parsePathId(pathId[2]);
                        if (id != -1) {
                            if (taskManager.getTaskById(id) != null) {
                                response = gson.toJson(taskManager.getTaskById(id));
                                sendText(httpExchange, 200, response);
                            } else {
                                sendNotFound(httpExchange, 404);
                            }
                        }
                    } else {
                        sendNotFound(httpExchange, 500);
                    }
                    break;
                }

                case "POST": {
                    if (Pattern.matches("^/tasks$", path)) {
                        String inputTask = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task taskFromJson = gson.fromJson(inputTask, Task.class);
                        try {
                            if (taskFromJson.getUid() != null) {
                                if (taskManager.getTaskById(taskFromJson.getUid()) != null) {
                                    taskManager.updateTask(taskFromJson);
                                    response = "Задача обновлена.";
                                    sendText(httpExchange, 201, gson.toJson(response));
                                } else {
                                    sendNotFound(httpExchange, 405);
                                }
                            } else {
                                Task newTask = taskManager.createTask(taskFromJson);
                                response = "Новая задача создана.";
                                sendText(httpExchange, 201, gson.toJson(response));
                            }
                        } catch (ValidationException e) {
                            errorHandler.handle(httpExchange, e);
                        }
                    } else {
                        sendNotFound(httpExchange, 405);
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String[] pathId = path.split("/");
                        id = parsePathId(pathId[2]);
                        if (id != -1) {
                            taskManager.delTaskById(id);
                            sendDelete(httpExchange, 204);
                        }
                    } else {
                        sendNotFound(httpExchange, 404);
                    }
                    break;
                }
                default: {
                    sendNotFound(httpExchange, 405);
                }
            }

        } catch (Exception e) {
            errorHandler.handle(httpExchange, e);
        } finally {
            httpExchange.close();
        }
    }
}
