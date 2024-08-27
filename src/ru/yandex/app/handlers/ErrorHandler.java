package ru.yandex.app.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.app.exceptions.ManagerSaveException;
import ru.yandex.app.exceptions.NotFoundException;
import ru.yandex.app.exceptions.ValidationException;

import java.io.IOException;

public class ErrorHandler extends BaseHttpHandler {

    Gson gson;

    public ErrorHandler(Gson gson) {
        this.gson = gson;
    }

    public void handle(HttpExchange h, Exception e) throws IOException {
        sendText(h, 500, gson.toJson(e));
    }

    public void handle(HttpExchange h, ManagerSaveException e) throws IOException {
        sendText(h, 400, gson.toJson(e));
    }

    public void handle(HttpExchange h, NotFoundException e) throws IOException {
        sendText(h, 404, gson.toJson(e));
    }

    public void handle(HttpExchange h, ValidationException e) throws IOException {
        sendHasInteractions(h, 406);
    }


}
