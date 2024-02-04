package ru.yandex.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Epic;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("InMemoryTaskManagerTest")
class InMemoryTaskManagerTest {

    @DisplayName("size should be equals number of tasks")
    @Test
    void getAllTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        inMemoryTaskManager.createTask(new Task());
        inMemoryTaskManager.createTask(new Task());
        assertEquals(2, inMemoryTaskManager.getAllTasks().size(), "should be equals");
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
        assertNotEquals(0, newTask.getUid(), "should be !equal 0");
    }
    @DisplayName("Task IDs should be same")
    @Test
    void updateTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task newTask = inMemoryTaskManager.createTask(new Task("Task N1"));
        int newTaskId = newTask.getUid();

        newTask.setName("Task N1A");
        newTask.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(newTask);
        int newTaskIdExpectedId = newTask.getUid();

        assertEquals(newTaskIdExpectedId,newTaskId, "IDs should be equals");
    }
    @DisplayName("SubTasks should be Null and Epic should have NEW status")
    @Test
    void delAllSubTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));
        inMemoryTaskManager.createSubTask(new SubTask("sub 2","desc", Status.DONE, epic1.getUid()));
        inMemoryTaskManager.delAllSubTasks();

        assertEquals(Status.NEW, epic1.getStatus(), "Epics status should be NEW");
        assertEquals(new ArrayList<>(), epic1.getSubTasksUids(), "subTasks should be Empty");
    }

    @DisplayName("Should return SubTask ID and increase tasksViewHistory")
    @Test
    void getSubTaskById() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));
        SubTask subTaskExpected = inMemoryTaskManager.getSubTaskById(subTask1.getUid());
        int tasksViewHistorySize = inMemoryTaskManager.getHistory().size();

        assertNotNull(subTaskExpected);
        assertEquals(1, tasksViewHistorySize,  "size should not be empty");
    }
    @DisplayName("Should return SubTask and put SubTask ID in Epic")
    @Test
    void createSubTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));

        assertNotEquals(0, subTask1.getUid(), "ID should not be 0");
        assertTrue(epic1.getSubTasksUids().contains(subTask1.getUid()));
    }

    @DisplayName("Should return Null instead of SubTask")
    @Test
    void createSubTaskWithEmptyEpic() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, 2));
        assertNull(subTask1);
    }
    @DisplayName("Should be the same SubTask ID")
    @Test
    void updateSubTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));
        subTask1.setName("sub 1A");
        subTask1.setDescription("new desc");
        inMemoryTaskManager.updateSubTask(subTask1);
        SubTask subTaskExpected = inMemoryTaskManager.getSubTaskById(subTask1.getUid());

        assertEquals(subTaskExpected.getUid(), subTask1.getUid(), "IDs should be equals");
    }
    @DisplayName("Subtasks and Epic shouldn't contain deleted subTaskId")
    @Test
    void delSubTaskById() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));
        int subTask1Id = subTask1.getUid();
        inMemoryTaskManager.delSubTaskById(subTask1Id);

        assertFalse(epic1.getSubTasksUids().contains(subTask1.getUid()));
        assertFalse(inMemoryTaskManager.getAllSubTasks().contains(subTask1));
    }

    @DisplayName("should return Epic and increase tasksViewHistory")
    @Test
    void getEpicById() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        Epic epicExpected = inMemoryTaskManager.getEpicById(epic1.getUid());
        String taskType = epicExpected.getClass().getSimpleName();

        assertNotNull(epicExpected);
        assertEquals("Epic", taskType,  "both should be Epic");
    }

    @DisplayName("result shouldn't be empty, with ID and Epic type")
    @Test
    void createEpic() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        String taskType = epic1.getClass().getSimpleName();

        assertNotNull(epic1);
        assertNotEquals(0, epic1.getUid(), "ID should not be 0");
        assertEquals("Epic", taskType,  "both should be Epic");
    }

    @DisplayName("Should be the same Epic ID and Epic Status")
    @Test
    void updateEpic() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        epic1.setName("Epic 11B");
        epic1.setDescription("new desc");
        inMemoryTaskManager.updateEpic(epic1);
        Epic epicExpected = inMemoryTaskManager.getEpicById(epic1.getUid());

        assertEquals(epicExpected.getUid(), epic1.getUid(), "IDs should be equals");
        assertEquals(epicExpected.getStatus(), epic1.getStatus(), " Status should be equals");
    }

    @DisplayName("Should delete Epic and  all Epics tasks")
    @Test
    void delEpicById() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));
        SubTask subTask2 = inMemoryTaskManager.createSubTask(new SubTask("sub 2","desc", Status.DONE, epic1.getUid()));
        inMemoryTaskManager.delEpicById(epic1.getUid());

        assertNull(inMemoryTaskManager.getEpicById(epic1.getUid()));
        assertNull(inMemoryTaskManager.getSubTaskById(subTask1.getUid()), "should be null");
        assertNull(inMemoryTaskManager.getSubTaskById(subTask2.getUid()), "should be null");
        assertFalse(inMemoryTaskManager.getAllSubTasks().contains(subTask1));
        assertFalse(inMemoryTaskManager.getAllSubTasks().contains(subTask2));
    }

    @DisplayName("Epic ID in subtasks should be equals Epic ID")
    @Test
    void getListOfEpicSubTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));
        SubTask subTask2 = inMemoryTaskManager.createSubTask(new SubTask("sub 2","desc", Status.DONE, epic1.getUid()));
        List<SubTask> list = inMemoryTaskManager.getListOfEpicSubTasks(epic1.getUid());
        boolean isEqual1 = list.get(list.indexOf(subTask1)).getEpicId() == subTask1.getEpicId();
        boolean isEqual2 = list.get(list.indexOf(subTask2)).getEpicId() == subTask2.getEpicId();
        assertTrue(isEqual1, "should be equals");
        assertTrue(isEqual2, "should be equals");
    }

    @DisplayName("should return not empty tasksViewHistory if get method was called")
    @Test
    void getHistory() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("sub 1","desc", Status.DONE, epic1.getUid()));
        Epic epicExpected = inMemoryTaskManager.getEpicById(epic1.getUid());
        SubTask subTaskExpected = inMemoryTaskManager.getSubTaskById(subTask1.getUid());
        int tasksViewHistorySize = inMemoryTaskManager.getHistory().size();

        assertNotNull(subTaskExpected);
        assertEquals(2, tasksViewHistorySize,  "size should not be empty");
    }
}