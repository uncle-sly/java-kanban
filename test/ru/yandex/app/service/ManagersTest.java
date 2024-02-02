package ru.yandex.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("ManagersTest")
class ManagersTest {
    @DisplayName("Should return initialised TaskManager")
    @Test
    void getDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        assertNotNull(taskManager);
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @DisplayName("Should return initialised HistoryManager")
    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }
}