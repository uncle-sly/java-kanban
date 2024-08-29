package ru.yandex.app.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.app.model.RequestMethod;
import ru.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final ErrorHandler errorHandler;

    public HistoryHandler(TaskManager taskManager, ErrorHandler errorHandler) {
        this.taskManager = taskManager;
        this.errorHandler = errorHandler;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        try {
            String path = httpExchange.getRequestURI().getPath();
            String response;
            RequestMethod method = RequestMethod.valueOf(httpExchange.getRequestMethod());

            if (method.equals(RequestMethod.GET) && (Pattern.matches("^/history$", path))) {
                response = gson.toJson(taskManager.getHistory());
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
