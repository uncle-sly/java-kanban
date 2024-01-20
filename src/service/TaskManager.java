package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int seq = 0;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    private int genId() {
        return ++seq;
    }

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    // Tasks
    public ArrayList<Task> getAllTasks() {
        if (tasks.values().isEmpty()) {
            return null;
        }
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
        if (subTasks.values().isEmpty()) {
            return null;
        }
        return new ArrayList<>(subTasks.values());
    }

    public void delAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            calculateStatus(epic);
        }
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public SubTask createSubTask(SubTask subTask) {
        subTask.setUid(genId());
        subTasks.put(subTask.getUid(), subTask);

        Epic epic = subTask.getEpic();
        Epic currentEpic = epics.get(epic.getUid());
        if (currentEpic == null) {
            return null;
        }
        currentEpic.getSubTasks().add(subTask);
        calculateStatus(subTask.getEpic());
        return subTask;
    }

    public void updateSubTask(SubTask subTask) {
        SubTask currentSubTask = subTasks.get(subTask.getUid());
        currentSubTask.setName(subTask.getName());
        currentSubTask.setStatus(subTask.getStatus());
        currentSubTask.setDescription(subTask.getDescription());

        Epic epic = subTask.getEpic();
        Epic currentEpic = epics.get(epic.getUid());
        if (currentEpic == null) {
            return;
        }
        currentEpic.getSubTasks().set(epic.getSubTasks().indexOf(currentSubTask), subTask);
        calculateStatus(currentEpic);
    }

    public void delSubTaskById(int id) {
        SubTask removedSubTask = subTasks.remove(id);
        Epic epicRemovedSubTask = removedSubTask.getEpic();
        Epic existEpic = epics.get(epicRemovedSubTask.getUid());
        existEpic.getSubTasks().remove(removedSubTask);
        calculateStatus(existEpic);
    }

    //Epics
    public ArrayList<Epic> getAllEpics() {
        if (epics.values().isEmpty()) {
            return null;
        }
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
        calculateStatus(epics.get(epic.getUid()));
        return epic;
    }

    public void updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getUid());
        if (currentEpic == null) return;
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
    }

    public void delEpicById(int id) {
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpic().getUid() == id) {
                subTasks.remove(subTask.getUid());
            }
        }
        epics.remove(id);
    }

    public ArrayList<SubTask> getListOfEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    private void calculateStatus(Epic epic) {
        Epic existedEpic = epics.get(epic.getUid());
        ArrayList<SubTask> subTasks = existedEpic.getSubTasks();
        int a = 0, b = 0, c = 0;
        String epicStatus;
        if (subTasks.isEmpty()) {
            existedEpic.setStatus("NEW");
        } else {
            for (SubTask task : subTasks) {
                if (task.getStatus().equals("NEW")) {
                    a++;
                } else if (task.getStatus().equals("IN_PROGRESS")) {
                    b++;
                } else {
                    c++;
                }
            }
            if ((a != 0) && (b == 0) && (c == 0)) {
                epicStatus = "NEW";
            } else if ((a == 0) && (b == 0) && (c != 0)) {
                epicStatus = "DONE";
            } else {
                epicStatus = "IN_PROGRESS";
            }
            existedEpic.setStatus(epicStatus);
        }
    }
}
