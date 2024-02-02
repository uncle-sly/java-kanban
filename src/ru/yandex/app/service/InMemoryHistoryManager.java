package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> tasksViewHistory = new LinkedList<>();

    @Override
    public void addInTasksViewHistory(Task task) {
        int listSize = tasksViewHistory.size();
        int capacity = 10;

/*        if (listSize<10) {
            tasksViewHistory.addLast(task);
        } else {
            for (int i = 0; i < listSize-2; i++) {
                tasksViewHistory.add(tasksViewHistory.get(i + 1));
                if (i==listSize-2) {tasksViewHistory.set(i,task);}
            }
        }*/
        if (listSize == capacity) {
            tasksViewHistory.removeFirst();
            tasksViewHistory.add(task);
        } else {
            tasksViewHistory.add(task);
        }

    }

    @Override
    public List<Task> getHistory() {
        return tasksViewHistory;
    }
}
