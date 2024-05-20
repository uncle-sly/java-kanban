package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.List;

public interface HistoryManager {


    void add(Task task);

    List<Task> getAll();

    void remove(int id);
}

