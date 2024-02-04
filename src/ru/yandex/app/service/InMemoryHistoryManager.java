package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> tasksViewHistory = new LinkedList<>();
    private static final int HISTORY_CAPACITY = 10;

    @Override
    public void addInTasksViewHistory(Task task) {
        int historySize = tasksViewHistory.size();

        if (historySize == HISTORY_CAPACITY) {
            tasksViewHistory.removeFirst();
        }
        tasksViewHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return  new ArrayList<>(tasksViewHistory);
    }
}
