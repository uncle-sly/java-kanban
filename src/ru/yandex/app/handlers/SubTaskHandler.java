package ru.yandex.app.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.app.exceptions.ValidationException;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;
    Gson gson;
    ErrorHandler errorHandler;

    public SubTaskHandler(TaskManager taskManager, Gson gson, ErrorHandler errorHandler) {
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
                    if (Pattern.matches("^/subtasks$", path)) {
                        response = gson.toJson(taskManager.getAllSubTasks());
                        sendText(httpExchange, 200, response);
                    } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String[] pathId = path.split("/");
                        id = parsePathId(pathId[2]);
                        if (id != -1) {
                            if (taskManager.getSubTaskById(id) != null) {
                                response = gson.toJson(taskManager.getSubTaskById(id));
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
                    if (Pattern.matches("^/subtasks$", path)) {
                        String inputTask = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        SubTask taskFromJson = gson.fromJson(inputTask, SubTask.class);
                        try {
                            if (taskFromJson.getUid() != null) {
                                if (taskManager.getSubTaskById(taskFromJson.getUid()) != null) {
                                    taskManager.updateSubTask(taskFromJson);
                                    response = "Подзадача обновлена.";
                                    sendText(httpExchange, 201, gson.toJson(response));
                                } else {
                                    sendNotFound(httpExchange, 405);
                                }
                            } else {
                                SubTask newTask = taskManager.createSubTask(taskFromJson);
                                response = "Новая подзадача создана.";
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
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String[] pathId = path.split("/");
                        id = parsePathId(pathId[2]);
                        if (id != -1) {
                            taskManager.delSubTaskById(id);
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
