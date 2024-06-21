package ru.yandex.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("InMemoryHistoryManagerTest")
class InMemoryHistoryManagerTest {

    @DisplayName("TasksViewHistory should be not Empty")
    @Test
    void addInTasksViewHistory() {
        Task task = new Task();
        InMemoryHistoryManager history = new InMemoryHistoryManager();

        history.add(task);
        int listSize = history.getAll().size();

        assertNotEquals(listSize, 0, "TasksViewHistory should be not Empty");
    }

    @DisplayName("delete at the beginning")
    @Test
    void testRemoveFirst() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1");
        task1.setUid(1);
        manager.add(task1);
        Task task2 = new Task("Task 2");
        task2.setUid(2);
        manager.add(task2);
        Task task3 = new Task("Task 3");
        task3.setUid(3);
        manager.add(task3);

        manager.remove(task1.getUid());
        assertEquals(List.of(task2, task3), manager.getAll(), "should be task 2 and 3");
    }

    @DisplayName("delete at the middle")
    @Test
    void testRemoveMiddle() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1");
        task1.setUid(1);
        manager.add(task1);
        Task task2 = new Task("Task 2");
        task2.setUid(2);
        manager.add(task2);
        Task task3 = new Task("Task 3");
        task3.setUid(3);
        manager.add(task3);

        manager.remove(task2.getUid());
        manager.getAll();
        assertEquals(List.of(task1, task3), manager.getAll(), "should be Task 1 and 3");
    }

    @DisplayName("delete at the end")
    @Test
    void testRemoveEnd() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1");
        task1.setUid(1);
        manager.add(task1);
        Task task2 = new Task("Task 2");
        task2.setUid(2);
        manager.add(task2);
        Task task3 = new Task("Task 3");
        task3.setUid(3);
        manager.add(task3);

        manager.remove(task3.getUid());
        assertEquals(manager.getAll(), List.of(task1, task2), "should be Task 1 and 2");
    }

    @DisplayName("delete 2 tasks at the middle")
    @Test
    void testRemoveMiddle2() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1");
        task1.setUid(1);
        manager.add(task1);
        Task task2 = new Task("Task 2");
        task2.setUid(2);
        manager.add(task2);
        Task task3 = new Task("Task 3");
        task3.setUid(3);
        manager.add(task3);
        Task task4 = new Task("Task 4");
        task4.setUid(4);
        manager.add(task4);

        manager.remove(task2.getUid());
        manager.remove(task3.getUid());
        assertEquals(manager.getAll(), List.of(task1, task4), "should be task 1 and 4");
    }

    @DisplayName("delete 1 task and check 3 element goes after 1")
    @Test
    void testRemovedNodeCheckSequence() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1");
        task1.setUid(1);
        manager.add(task1);
        Task task2 = new Task("Task 2");
        task2.setUid(2);
        manager.add(task2);
        Task task3 = new Task("Task 3");
        task3.setUid(3);
        manager.add(task3);

        manager.remove(task2.getUid());

        assertEquals(manager.getAll().get(0), task1, "First element should be Task1");
        assertEquals(manager.getAll().get(1), task3, "Third element should be Task3");
    }

    @DisplayName("delete 1 task and check that we can't get him")
    @Test
    void testCantGetRemovedNode() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1");
        task1.setUid(1);
        manager.add(task1);
        Task task2 = new Task("Task 2");
        task2.setUid(2);
        manager.add(task2);
        Task task3 = new Task("Task 3");
        task3.setUid(3);
        manager.add(task3);

        manager.remove(task3.getUid());
        assertFalse(manager.getAll().contains(task3));
    }

}