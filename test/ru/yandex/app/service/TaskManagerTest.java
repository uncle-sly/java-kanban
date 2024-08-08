package ru.yandex.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.exceptions.NotFoundException;
import ru.yandex.app.exceptions.ValidationException;
import ru.yandex.app.model.Epic;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    Task task;

    protected abstract T createManager();

    @BeforeEach
    void init() {

        taskManager = createManager();

    }


    @DisplayName("size should be equals number of tasks")
    @Test
    void getAllTasks() {
        taskManager.createTask(new Task("New Task 1", "", Status.NEW,
                Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00")));
        taskManager.createTask(new Task("New Task 2", "", Status.NEW,
                Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:11")));

        assertEquals(2, taskManager.getAllTasks().size(), "should be equals");
    }

    @DisplayName("Tasks should be equals by ID")
    @Test
    void getTaskById() {
        Task newTask = taskManager.createTask(new Task("New Task 1", "", Status.NEW,
                Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00")));
        Task expectedTask = taskManager.getTaskById(newTask.getUid());

        assertEquals(expectedTask.getUid(), newTask.getUid(), "should be equals by ID");
    }

    @DisplayName("Task should be not Empty and with ID")
    @Test
    void createTask() {
        Task newTask = taskManager.createTask(new Task("New Task 1", "", Status.NEW,
                Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00")));

        assertNotNull(newTask, "should be Not Null");
        assertNotEquals(0, newTask.getUid(), "should be !equal 0");
    }

    @DisplayName("Task IDs should be same")
    @Test
    void updateTask() {
        Task newTask = taskManager.createTask(new Task("New Task 1", "", Status.NEW,
                Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00")));
        int newTaskId = newTask.getUid();

        newTask.setName("Task N1A");
        newTask.setStatus(Status.DONE);
        taskManager.updateTask(newTask);
        int newTaskIdExpectedId = newTask.getUid();

        assertEquals(newTaskIdExpectedId, newTaskId, "IDs should be equals");
    }

    @DisplayName("SubTasks should be Null and Epic should have NEW status")
    @Test
    void delAllSubTasks() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), epic1.getUid()));
        taskManager.createSubTask(new SubTask("sub 2", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-03-01T10:00"), epic1.getUid()));
        taskManager.delAllSubTasks();

        assertEquals(Status.NEW, epic1.getStatus(), "Epics status should be NEW");
        assertEquals(new ArrayList<>(), epic1.getSubTasksUids(), "subTasks should be Empty");
    }

    @DisplayName("Should return SubTask ID and increase tasksViewHistory")
    @Test
    void getSubTaskById() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.of(2020, Month.JANUARY, 1, 10, 0), epic1.getUid()));
        SubTask subTaskExpected = taskManager.getSubTaskById(subTask1.getUid());
        int tasksViewHistorySize = taskManager.getHistory().size();

        assertNotNull(subTaskExpected);
        assertEquals(1, tasksViewHistorySize, "size should not be empty");
    }

    @DisplayName("Should return SubTask and put SubTask ID in Epic")
    @Test
    void createSubTask() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), epic1.getUid()));

        assertNotEquals(0, subTask1.getUid(), "ID should not be 0");
        assertTrue(epic1.getSubTasksUids().contains(subTask1.getUid()));
    }

    @DisplayName("Should return Null instead of SubTask")
    @Test
    void createSubTaskWithEmptyEpic() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                {
                    SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc",
                            Status.DONE, Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), 2));
                }
        );
        assertEquals("Can't get Epic: 2 Not found.", thrown.getMessage());
    }

    @DisplayName("Should be the same SubTask ID")
    @Test
    void updateSubTask() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), epic1.getUid()));
        subTask1.setName("sub 1A");
        subTask1.setDescription("new desc");
        taskManager.updateSubTask(subTask1);
        SubTask subTaskExpected = taskManager.getSubTaskById(subTask1.getUid());

        assertEquals(subTaskExpected.getUid(), subTask1.getUid(), "IDs should be equals");
    }

    @DisplayName("Subtasks and Epic shouldn't contain deleted subTaskId")
    @Test
    void delSubTaskById() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), epic1.getUid()));
        int subTask1Id = subTask1.getUid();
        taskManager.delSubTaskById(subTask1Id);

        assertFalse(epic1.getSubTasksUids().contains(subTask1.getUid()));
        assertFalse(taskManager.getAllSubTasks().contains(subTask1));
    }

    @DisplayName("should return Epic and increase tasksViewHistory")
    @Test
    void getEpicById() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        Epic epicExpected = taskManager.getEpicById(epic1.getUid());
        String taskType = epicExpected.getClass().getSimpleName();

        assertNotNull(epicExpected);
        assertEquals("Epic", taskType, "both should be Epic");
    }

    @DisplayName("result shouldn't be empty, with ID and Epic type")
    @Test
    void createEpic() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        String taskType = epic1.getClass().getSimpleName();

        assertNotNull(epic1);
        assertNotEquals(0, epic1.getUid(), "ID should not be 0");
        assertEquals("Epic", taskType, "both should be Epic");
    }

    @DisplayName("Should be the same Epic ID and Epic Status")
    @Test
    void updateEpic() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        epic1.setName("Epic 11B");
        epic1.setDescription("new desc");
        taskManager.updateEpic(epic1);
        Epic epicExpected = taskManager.getEpicById(epic1.getUid());

        assertEquals(epicExpected.getUid(), epic1.getUid(), "IDs should be equals");
        assertEquals(epicExpected.getStatus(), epic1.getStatus(), " Status should be equals");
    }

    @DisplayName("Should delete Epic and  all Epics tasks")
    @Test
    void delEpicById() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), epic1.getUid()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("sub 2", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-02T10:00"), epic1.getUid()));
        taskManager.delEpicById(epic1.getUid());

        assertNull(taskManager.getEpicById(epic1.getUid()));
        assertNull(taskManager.getSubTaskById(subTask1.getUid()), "should be null");
        assertNull(taskManager.getSubTaskById(subTask2.getUid()), "should be null");
        assertFalse(taskManager.getAllSubTasks().contains(subTask1));
        assertFalse(taskManager.getAllSubTasks().contains(subTask2));
    }

    @DisplayName("Epic ID in subtasks should be equals Epic ID")
    @Test
    void getListOfEpicSubTasks() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), epic1.getUid()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("sub 2", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-02T10:00"), epic1.getUid()));
        List<SubTask> list = taskManager.getListOfEpicSubTasks(epic1.getUid());
        boolean isEqual1 = list.get(list.indexOf(subTask1)).getEpicId() == subTask1.getEpicId();
        boolean isEqual2 = list.get(list.indexOf(subTask2)).getEpicId() == subTask2.getEpicId();
        assertTrue(isEqual1, "should be equals");
        assertTrue(isEqual2, "should be equals");
    }

    @DisplayName("should return not empty tasksViewHistory if get method was called")
    @Test
    void getHistory() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2020-01-01T10:00"), epic1.getUid()));
        Epic epicExpected = taskManager.getEpicById(epic1.getUid());
        SubTask subTaskExpected = taskManager.getSubTaskById(subTask1.getUid());
        int tasksViewHistorySize = taskManager.getHistory().size();

        assertNotNull(subTaskExpected);
        assertEquals(2, tasksViewHistorySize, "size should not be empty");
    }

    @DisplayName("проверяем, что нет пересечения с самим собой")
    @Test
    void checkThatThereIsNoOverlapWithItself() {
        task = taskManager.createTask(new Task("File Backed Task 1", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T12:00")));

        task.setDescription("Task N1 for FileBackedTaskManager");
        task.setStatus(Status.IN_PROGRESS);
        task.setStartTime(LocalDateTime.parse("2024-06-01T12:15"));
        taskManager.updateTask(task);

        assertDoesNotThrow(() -> taskManager.updateTask(task));
    }

    @DisplayName("проверяем, что при изменении SubTask происходит перерасчет полей Epic")
    @Test
    void epicFieldsAreRecalculatedWhenSubTaskIsChanged() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 100"));
        SubTask sub1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T10:00"), epic1.getUid()));
        SubTask sub2 = taskManager.createSubTask(new SubTask("sub 2", "desc", Status.DONE,
                Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T15:00"), epic1.getUid()));

        assertEquals(epic1.getStatus(), Status.DONE);
        assertEquals(epic1.getStartTime(), LocalDateTime.parse("2024-01-01T10:00"));
        assertEquals(epic1.getDuration(), Duration.of(50, ChronoUnit.MINUTES));
        assertEquals(epic1.getEndTime(), LocalDateTime.parse("2024-01-01T15:30"));

        sub1.setStatus(Status.IN_PROGRESS);
        sub1.setDuration(Duration.of(40, ChronoUnit.MINUTES));
        sub1.setStartTime(LocalDateTime.parse("2024-01-01T11:00"));
        taskManager.updateSubTask(sub1);

        assertEquals(epic1.getStatus(), Status.IN_PROGRESS);
        assertEquals(epic1.getStartTime(), LocalDateTime.parse("2024-01-01T11:00"));
        assertEquals(epic1.getDuration(), Duration.of(70, ChronoUnit.MINUTES));
        assertEquals(epic1.getEndTime(), LocalDateTime.parse("2024-01-01T15:30"));
    }

    @DisplayName("проверяем, что происходит перерасчет времени оконания Epic после удаления SubTask")
    @Test
    void epicEndTimeRecalculatedAfterSubTaskIsDeleted() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 100"));
        SubTask sub1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T10:00"), epic1.getUid()));
        SubTask sub2 = taskManager.createSubTask(new SubTask("sub 2", "desc", Status.IN_PROGRESS,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T15:00"), epic1.getUid()));
        SubTask sub3 = taskManager.createSubTask(new SubTask("sub 3", "desc", Status.DONE,
                Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T20:00"), epic1.getUid()));

//        время окончания epic1 = 20.30 (sub3)
        assertEquals(epic1.getEndTime(), sub3.getEndTime());

        taskManager.delSubTaskById(sub3.getUid());
//        время окончания epic1 = 15.20 (sub2)
        assertEquals(epic1.getEndTime(), sub2.getEndTime());

        taskManager.delSubTaskById(sub2.getUid());
//        время окончания epic1 = 10.10 (sub1)
        assertEquals(epic1.getEndTime(), sub1.getEndTime());
    }

    @DisplayName("проверяем, что происходит перерасчет продолжительности Epic после удаления SubTask")
    @Test
    void epicDurationRecalculatedAfterSubTaskIsDeleted() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 100"));
        SubTask sub1 = taskManager.createSubTask(new SubTask("sub 1", "desc", Status.DONE,
                Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T10:00"), epic1.getUid()));
        SubTask sub2 = taskManager.createSubTask(new SubTask("sub 2", "desc", Status.IN_PROGRESS,
                Duration.of(60, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T15:00"), epic1.getUid()));
        SubTask sub3 = taskManager.createSubTask(new SubTask("sub 3", "desc", Status.DONE,
                Duration.of(90, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T20:00"), epic1.getUid()));

//        продолжительность epic1 = 180
        assertEquals(epic1.getDuration(), Duration.of(180, ChronoUnit.MINUTES));

        taskManager.delSubTaskById(sub3.getUid());
//        продолжительность epic1 = 90
        assertEquals(epic1.getDuration(), Duration.of(90, ChronoUnit.MINUTES));

        taskManager.delSubTaskById(sub2.getUid());
//        продолжительность epic1 = 30
        assertEquals(epic1.getDuration(), Duration.of(30, ChronoUnit.MINUTES));

        taskManager.delSubTaskById(sub1.getUid());
//        продолжительность epic1 = 0
        assertEquals(epic1.getDuration(), Duration.of(0, ChronoUnit.MINUTES));
    }

    @DisplayName("Проверяем граничные ситуации, не должно быть пересечения. Задача добавлена До всех задач, после и между задачами.")
    @Test
    void checkingBoundarySituations() {
        taskManager.createTask(new Task("FB Task 1", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T12:00")));
        taskManager.createTask(new Task("FB Task 2", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T13:00")));
        taskManager.createTask(new Task("FB Task 3", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T14:00")));
//        до всех
        assertDoesNotThrow(() -> taskManager.createTask(new Task("FB Task 4", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T11:00"))));
//        после всех
        assertDoesNotThrow(() -> taskManager.createTask(new Task("FB Task 5", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T15:00"))));
//        между задачами
        assertDoesNotThrow(() -> taskManager.createTask(new Task("FB Task 6", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T12:30"))));
    }

    @DisplayName("Проверяем пересечения. Задача пересекатеся с началом другой задачи, с окончанием, или попадает внутрь интервала другой задачи.")
    @Test
    void checkingTaskIntersections() {
        taskManager.createTask(new Task("FB Task 1", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T12:00")));
        taskManager.createTask(new Task("FB Task 2", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T13:00")));
        taskManager.createTask(new Task("FB Task 3", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T14:00")));

//        пересекается с началом другой задачи
        ValidationException thrown1 = assertThrows(ValidationException.class, () -> taskManager.createTask(
                new Task("FB Task 4", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T12:45"))
        ));
        assertNotNull(thrown1);

//        пересекается с окончанием другой задачи
        ValidationException thrown2 = assertThrows(ValidationException.class, () -> taskManager.createTask(
                new Task("FB Task 5", "", Status.NEW, Duration.of(30, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T14:25"))
        ));
        assertNotNull(thrown2);

//        попадает внутрь другой задаи
        ValidationException thrown3 = assertThrows(ValidationException.class, () -> taskManager.createTask(
                new Task("FB Task 6", "", Status.NEW, Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-06-01T13:10"))
        ));
        assertNotNull(thrown3);
    }

}