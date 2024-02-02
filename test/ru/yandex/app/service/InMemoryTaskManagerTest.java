package ru.yandex.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Epic;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("InMemoryTaskManagerTest")
class InMemoryTaskManagerTest {

    @DisplayName("size should be equals number of tasks")
    @Test
    void getAllTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        inMemoryTaskManager.createTask(new Task());
        inMemoryTaskManager.createTask(new Task());
        assertEquals(inMemoryTaskManager.getAllTasks().size(),2, "should be equals");
    }

    @DisplayName("Tasks should be equals by ID")
    @Test
    void getTaskById() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task newTask = inMemoryTaskManager.createTask(new Task("Task N1"));
        Task expectedTask = inMemoryTaskManager.getTaskById(newTask.getUid());

        assertEquals(expectedTask.getUid(), newTask.getUid(), "should be equals by ID");
    }
    @DisplayName("Task should be not Empty and with ID")
    @Test
    void createTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task newTask = inMemoryTaskManager.createTask(new Task("Task N1"));

        assertNotNull(newTask, "should be Not Null");
        assertNotEquals(newTask.getUid(),0, "should be !equal 0");
    }

    @Test
    void updateTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task newTask = inMemoryTaskManager.createTask(new Task("Task N1"));
        int newTaskId = newTask.getUid();

        newTask.setName("Task N1A");
        newTask.setStatus("DONE");
        inMemoryTaskManager.updateTask(newTask);
        int newTaskIdExpectedId = newTask.getUid();

        assertEquals(newTaskIdExpectedId,newTaskId, "IDs should be equals");
    }
    @DisplayName("SubTasks should ne Null and Epic should have NEW status")
    @Test
    void delAllSubTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", "DONE", epic1.getUid()));
        inMemoryTaskManager.createSubTask(new SubTask("sub 2","desc", "DONE", epic1.getUid()));
        inMemoryTaskManager.delAllSubTasks();

        assertEquals("NEW", epic1.getStatus(), "Epics status should ne NEW");
        assertEquals(new ArrayList<>(), epic1.getSubTasksUids(), "subTasks should be Empty");
    }

    @DisplayName("Should return SubTask ID and increase tasksViewHistory")
    @Test
    void getSubTaskById() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", "DONE", epic1.getUid()));
        SubTask subTaskExpected = inMemoryTaskManager.getSubTaskById(subTask1.getUid());
        int tasksViewHistorySize = inMemoryTaskManager.getHistory().size();

        assertNotNull(subTaskExpected);
        assertEquals(tasksViewHistorySize, 1,  "size should not be empty");
    }
    @DisplayName("Should return SubTask and put SubTask ID in Epic")
    @Test
    void createSubTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", "DONE", epic1.getUid()));

        assertNotEquals(0, subTask1.getUid(), "ID should not be 0");
        assertTrue(epic1.getSubTasksUids().contains(subTask1.getUid()));
    }

    @DisplayName("Should return Null instead of SubTask")
    @Test
    void createSubTaskWithEmptyEpic() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        //Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", "DONE", 2));
        assertNull(subTask1);
    }
    @DisplayName("Should be the same SubTask ID")
    @Test
    void updateSubTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", "DONE", epic1.getUid()));

        subTask1.setName("sub 1A");
        subTask1.setDescription("new desc");
        inMemoryTaskManager.updateSubTask(subTask1);
        SubTask subTaskExpected = inMemoryTaskManager.getSubTaskById(subTask1.getUid());

        assertEquals(subTaskExpected.getUid(),subTask1.getUid(), "IDs should be equals");
    }
    @DisplayName("subtasks and epic shouldn't contain subTaskId")
    @Test
    void delSubTaskById() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", "DONE", epic1.getUid()));
        int subTask1Id = subTask1.getUid();
        inMemoryTaskManager.delSubTaskById(subTask1Id);
        assertFalse(epic1.getSubTasksUids().contains(subTask1.getUid()));
        assertFalse(inMemoryTaskManager.getAllSubTasks().contains(subTask1));
    }

    @Test
    void getEpicById() {
    }

    @Test
    void createEpic() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void delEpicById() {
    }

    @Test
    void getListOfEpicSubTasks() {
    }

    @Test
    void getHistory() {
    }
}