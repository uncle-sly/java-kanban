package ru.yandex.app.service;

public class Managers {

    public static TaskManager getDefaultTaskManager() {
        return new FileBackedTaskManager(getDefaultHistory());
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    private Managers() {
    }
}
