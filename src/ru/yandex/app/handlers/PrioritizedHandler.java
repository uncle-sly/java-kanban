package ru.yandex.app.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    TaskManager taskManager;
    Gson gson;
    ErrorHandler errorHandler;

    public PrioritizedHandler(TaskManager taskManager, Gson gson, ErrorHandler errorHandler) {
        this.taskManager = taskManager;
        this.gson = gson;
        this.errorHandler = errorHandler;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            String response;

            if (requestMethod.equals("GET") && (Pattern.matches("^/prioritized$", path))) {
                response = gson.toJson(taskManager.getPrioritised());
                sendText(httpExchange, 200, response);
            } else {
                sendNotFound(httpExchange, 405);
            }

        } catch (Exception e) {
            errorHandler.handle(httpExchange, e);
        } finally {
            httpExchange.close();
        }
    }
}
