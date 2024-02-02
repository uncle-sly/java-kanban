package ru.yandex.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Task;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("InMemoryHistoryManagerTest")
class InMemoryHistoryManagerTest {

    @DisplayName("TasksViewHistory should be not Empty")
    @Test
    void addInTasksViewHistory() {
        Task task = new Task();
        InMemoryHistoryManager tasksViewHistory = new InMemoryHistoryManager();
        tasksViewHistory.addInTasksViewHistory(task);
        int listSize = tasksViewHistory.getHistory().size();

        assertNotEquals(listSize, 0, "TasksViewHistory should be not Empty");
    }

    @Test
    void shouldBe10ElementSizeTasksViewHistory() {
        Task task = new Task();
        InMemoryHistoryManager tasksViewHistory = new InMemoryHistoryManager();
        for (int i = 0; i < 15; i++) {
            tasksViewHistory.addInTasksViewHistory(task);
        }
        int listSize = tasksViewHistory.getHistory().size();

        System.out.println(listSize);
        assertEquals(listSize, 10, "should Be 10 Element Size");
    }
}