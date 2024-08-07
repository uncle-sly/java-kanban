package ru.yandex.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.app.model.Epic;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileBackedTaskManagerTest")
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    Path path = Paths.get("resources/test1_file_tasks.csv");
    Path path2 = Paths.get("resources/test2_file_tasks.csv");

    @Override
    public FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File(String.valueOf(path)));
    }

    @DisplayName("write tasks to file, file should be not Empty")
    @Test
    void writeTasksToFile() {
        TaskManager taskManager = new FileBackedTaskManager(new File(String.valueOf(path2)));

        Task task1 = taskManager.createTask(new Task("New Task 1", "", Status.NEW,
                Duration.of(10, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-01T10:00")));
        Task task2 = taskManager.createTask(new Task("New Task 2", "", Status.NEW,
                Duration.of(15, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-02T10:00")));
        Task task3 = taskManager.createTask(new Task("New Task 3", "", Status.NEW,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-03T10:00")));
        Epic epic1 = taskManager.createEpic(new Epic("New Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("New SubTask 1", "department 1", Status.NEW,
                Duration.of(20, ChronoUnit.MINUTES), LocalDateTime.parse("2024-01-04T10:00"), epic1.getUid()));

        assertTrue(Files.exists(path2));

        try {
            assertTrue(Files.size(path2) > 0);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        assertEquals(3, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(1, taskManager.getAllSubTasks().size());
    }

    @DisplayName("read tasks from file_tasks.csv, HashMaps should be not Empty")
    @Test
    void readTasksFromFile() {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(path2.toFile());
        assertEquals(3, taskManager.tasks.size());
        assertEquals(1, taskManager.epics.size());
        assertEquals(1, taskManager.subTasks.size());
    }

    @DisplayName("read uids from file, seq should be equal max uid")
    @Test
    void readUidsFromFile() {
        taskManager = FileBackedTaskManager.loadFromFile(path2.toFile());

        assertEquals(5, taskManager.seq);
    }

    @DisplayName("reload taskManager from file, should be equal with taskManager")
    @Test
    void reloadTaskManager() {
        taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
        FileBackedTaskManager reloadTaskManager = FileBackedTaskManager.loadFromFile(path.toFile());

        assertEquals(taskManager.getAllTasks(), reloadTaskManager.getAllTasks());
    }

    @DisplayName("create taskManager from empty file")
    @Test
    void createTaskManagerFromEmptyFile() {
        TaskManager taskManager;
        try {
            taskManager = FileBackedTaskManager.loadFromFile(File.createTempFile("empty", ".csv"));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @DisplayName("проверяем, что после восстановления файлового менеджера восстанавливается prioritisedTasks")
    @Test
    void checkingRecoveryPrioritisedTasksAfterFileManagerRecovery() {
        taskManager = FileBackedTaskManager.loadFromFile(path2.toFile());

        assertNotNull(taskManager.getPrioritised());
        assertEquals(4, taskManager.getPrioritised().size());
    }
}