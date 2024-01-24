package ru.yandex.app.service;

import ru.yandex.app.model.Epic;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int seq = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;


    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    // Tasks
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void delAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        task.setUid(genId());
        tasks.put(task.getUid(), task);
        return task;
    }

    public void updateTask(Task task) {
        Task currentTask = tasks.get(task.getUid());
        if (currentTask == null) return;
        currentTask.setName(task.getName());
        currentTask.setStatus(task.getStatus());
        currentTask.setDescription(task.getDescription());
    }

    public void delTaskById(int id) {
        tasks.remove(id);
    }

    //SubTasks
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void delAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksUids().clear();
            calculateStatus(epic.getUid());
        }
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public SubTask createSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic currentEpic = epics.get(epicId);
        if (currentEpic == null) {
            return null;
        }
        subTask.setUid(genId());
        subTasks.put(subTask.getUid(), subTask);
        currentEpic.getSubTasksUids().add(subTask.getUid());
        calculateStatus(subTask.getEpicId());
        return subTask;
    }

    public void updateSubTask(SubTask subTask) {
        SubTask currentSubTask = subTasks.get(subTask.getUid());
        currentSubTask.setName(subTask.getName());
        currentSubTask.setStatus(subTask.getStatus());
        currentSubTask.setDescription(subTask.getDescription());
        calculateStatus(subTask.getEpicId());
    }

    public void delSubTaskById(int id) {
        SubTask removedSubTask = subTasks.remove(id);
        int epicId = removedSubTask.getEpicId();
        Epic existEpic = epics.get(epicId);
        existEpic.getSubTasksUids().remove((Integer) id);
        calculateStatus(epicId);
    }

    //Epics
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void delAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        epic.setUid(genId());
        epics.put(epic.getUid(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getUid());
        if (currentEpic == null) return;
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
    }

    public void delEpicById(int id) {
        for (Integer subTask : epics.get(id).getSubTasksUids()) {
            subTasks.remove(subTask);
        }
        epics.remove(id);
    }

    public ArrayList<SubTask> getListOfEpicSubTasks(int id) {
        ArrayList<Integer> epicSubTasksUids = epics.get(id).getSubTasksUids();
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();

        for (Integer epicSubTasksUid : epicSubTasksUids) {
            epicSubTasks.add(subTasks.get(epicSubTasksUid));
        }
        return epicSubTasks;
    }

    private int genId() {
        return ++seq;
    }

    private void calculateStatus(int id) {
        Epic existedEpic = epics.get(id);
        ArrayList<Integer> epicSubTasksUids = existedEpic.getSubTasksUids();
        int newSubtasksCounter = 0;
        int doneSubtasksCounter = 0;
        String epicStatus;
        if (subTasks.isEmpty()) {
            existedEpic.setStatus("NEW");
        } else {
            for (Integer epicSubTasksUid : epicSubTasksUids) {
                final String status = subTasks.get(epicSubTasksUid).getStatus();

                if (status.equals("NEW")) {
                    newSubtasksCounter++;
                } else if (status.equals("IN_PROGRESS")) {
                    existedEpic.setStatus("IN_PROGRESS");
                    return;
                } else {
                    doneSubtasksCounter++;
                }
            }
            int size = epicSubTasksUids.size();
            if (newSubtasksCounter == size) {
                epicStatus = "NEW";
            } else if (doneSubtasksCounter == size) {
                epicStatus = "DONE";
            } else {
                epicStatus = "IN_PROGRESS";
            }
            existedEpic.setStatus(epicStatus);
        }
    }
}
