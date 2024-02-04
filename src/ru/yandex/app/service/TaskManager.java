package ru.yandex.app.service;

import ru.yandex.app.model.Epic;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.util.List;

public interface TaskManager {
    // Tasks
    List<Task> getAllTasks();

    void delAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void delTaskById(int id);

    //SubTasks
    List<SubTask> getAllSubTasks();

    void delAllSubTasks();

    SubTask getSubTaskById(int id);

    SubTask createSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void delSubTaskById(int id);

    //Epics
    List<Epic> getAllEpics();

    void delAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void delEpicById(int id);

    List<SubTask> getListOfEpicSubTasks(int id);

    List<Task> getHistory();
}
