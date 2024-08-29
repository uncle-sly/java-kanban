package ru.yandex.app.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.app.model.Epic;
import ru.yandex.app.model.RequestMethod;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final ErrorHandler errorHandler;

    public EpicHandler(TaskManager taskManager, ErrorHandler errorHandler) {
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
                    if (Pattern.matches("^/epics$", path)) {
                        response = gson.toJson(taskManager.getAllEpics());
                        sendText(httpExchange, 200, response);
                    } else if (Pattern.matches("^/epics/\\d+$", path)) {
                        String[] pathId = path.split("/");
                        id = parsePathId(pathId[2]);
                        if (id != -1) {
                            if (taskManager.getEpicById(id) != null) {
                                response = gson.toJson(taskManager.getEpicById(id));
                                sendText(httpExchange, 200, response);
                            } else {
                                sendNotFound(httpExchange, 404);
                            }
                        }
                    } else if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String[] pathId = path.split("/");
                        id = parsePathId(pathId[2]);
                        if (id != -1) {
                            if (taskManager.getListOfEpicSubTasks(id) != null) {
                                response = gson.toJson(taskManager.getListOfEpicSubTasks(id));
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

                case POST: {
                    if (Pattern.matches("^/epics$", path)) {
                        String inputTask = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic taskFromJson = gson.fromJson(inputTask, Epic.class);
                        if (taskFromJson.getUid() != null) {
                            if (taskManager.getEpicById(taskFromJson.getUid()) != null) {
                                taskManager.updateEpic(taskFromJson);
                                response = "Эпик обновлен.";
                                sendText(httpExchange, 201, gson.toJson(response));
                            } else {
                                sendNotFound(httpExchange, 405);
                            }
                        } else {
                            taskManager.createEpic(new Epic(taskFromJson.getName()));
                            response = "Новый Эпик создан.";
                            sendText(httpExchange, 201, gson.toJson(response));
                        }
                    } else {
                        sendNotFound(httpExchange, 405);
                    }
                    break;
                }
                case DELETE: {
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String[] pathId = path.split("/");
                        id = parsePathId(pathId[2]);
                        if (id != -1) {
                            taskManager.delEpicById(id);
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
