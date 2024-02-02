package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.List;

public interface HistoryManager {


    void addInTasksViewHistory(Task task);

    List<Task> getHistory();


}
