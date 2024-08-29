package ru.yandex.app.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.app.exceptions.ValidationException;
import ru.yandex.app.model.RequestMethod;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final ErrorHandler errorHandler;

    public SubTaskHandler(TaskManager taskManager, ErrorHandler errorHandler) {
        this.taskManager = taskManager;
        this.errorHandler = errorHandler;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            RequestMethod method = RequestMethod.valueOf(httpExchange.getRequestMethod());
            int id;
            String response;

            switch (method) {

                case GET: {
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
                        sendNotFound(httpExchange, 404);
                    }
                    break;
                }

                case POST: {
                    if (Pattern.matches("^/subtasks$", path)) {
                        String inputTask = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        SubTask taskFromJson;
                        if (!inputTask.isEmpty()) {
                            taskFromJson = gson.fromJson(inputTask, SubTask.class);
                        } else {
                            sendText(httpExchange, 500, gson.toJson("Empty Body"));
                            break;
                        }
                        try {
                            if (taskFromJson.getUid() != null) {
                                if (taskManager.getSubTaskById(taskFromJson.getUid()) != null) {
                                    taskManager.updateSubTask(taskFromJson);
                                    response = "Подзадача обновлена.";
                                    sendText(httpExchange, 201, gson.toJson(response));
                                } else {
                                    sendNotFound(httpExchange, 404);
                                }
                            } else {
                                taskManager.createSubTask(taskFromJson);
                                response = "Новая подзадача создана.";
                                sendText(httpExchange, 201, gson.toJson(response));
                            }
                        } catch (ValidationException e) {
                            errorHandler.handle(httpExchange, e);
                        }
                    } else {
                        sendNotFound(httpExchange, 404);
                    }
                    break;
                }
                case DELETE: {
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
