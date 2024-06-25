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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FileBackedTaskManagerTest")
class FileBackedTaskManagerTest {
    Path path = Paths.get("resources/test1_file_tasks.csv");

    @DisplayName("write tasks to file, file should be not Empty")
    @Test
    void writeTasksToFile() {
        TaskManager taskManager = new FileBackedTaskManager(new File(String.valueOf(path)));

        Task task1 = taskManager.createTask(new Task("New Task 1", "", Status.NEW));
        Task task2 = taskManager.createTask(new Task("New Task 2", "", Status.NEW));
        Task task3 = taskManager.createTask(new Task("New Task 3", "", Status.NEW));
        Epic epic1 = taskManager.createEpic(new Epic("New Epic 1"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("New SubTask 1", "department 1", Status.NEW, epic1.getUid()));

        assertTrue(Files.exists(path));

        try {
            assertTrue(Files.size(path) > 0);
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
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
        assertEquals(3, taskManager.tasks.size());
        assertEquals(1, taskManager.epics.size());
        assertEquals(1, taskManager.subTasks.size());
    }

    @DisplayName("read uids from file, seq should be equal max uid")
    @Test
    void readUidsFromFile() {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(new File("resources/test3_file_tasks.csv"));

        Task task1 = taskManager.createTask(new Task("New Task 1", "", Status.NEW));
        Task task2 = taskManager.createTask(new Task("New Task 2", "", Status.NEW));
        Task task3 = taskManager.createTask(new Task("New Task 3", "", Status.NEW));

        assertEquals(3, taskManager.seq);
    }

    @DisplayName("reload taskManager from file, should be equal with taskManager")
    @Test
    void reloadTaskManager() {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
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

}