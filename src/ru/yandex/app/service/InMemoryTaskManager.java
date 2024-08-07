package ru.yandex.app.service;

import ru.yandex.app.exceptions.NotFoundException;
import ru.yandex.app.exceptions.ValidationException;
import ru.yandex.app.model.Epic;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int seq = 0;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subTasks;
    private final HistoryManager history;

    protected final TreeSet<Task> prioritisedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));



    public InMemoryTaskManager(HistoryManager history) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.history = history;
    }

    // Tasks
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void delAllTasks() {
        for (Task task : getAllTasks()) {
            history.remove(task.getUid());
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        if (task != null) {
            history.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        task.setUid(genId());
        checkIntersectionOfTasksTimes(task);
        prioritisedTasks.add(task);
        tasks.put(task.getUid(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        Task currentTask = tasks.get(task.getUid());
        if (currentTask == null) {
            throw new NotFoundException("Can't get Task: " + task.getUid() + " Not found.");
        }
        checkIntersectionOfTasksTimes(task);
        prioritisedTasks.remove(currentTask);

        currentTask.setName(task.getName());
        currentTask.setStatus(task.getStatus());
        currentTask.setDescription(task.getDescription());
        prioritisedTasks.add(currentTask);
    }

    @Override
    public void delTaskById(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    //SubTasks
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void delAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasksUids().clear();
            calculateStatus(epic.getUid());
        }
        for (SubTask subTask : getAllSubTasks()) {
            history.remove(subTask.getUid());
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        final SubTask subTask = subTasks.get(id);

        if (subTask != null) {
            history.add(subTask);
        }
        return subTask;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Epic currentEpic = epics.get(epicId);
        if (currentEpic == null) {
            throw new NotFoundException("Can't get Epic: " + epicId + " Not found.");
        }
        subTask.setUid(genId());
        checkIntersectionOfTasksTimes(subTask);
        prioritisedTasks.add(subTask);
        subTasks.put(subTask.getUid(), subTask);
        currentEpic.getSubTasksUids().add(subTask.getUid());
        calculateStatus(subTask.getEpicId());
        return subTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask currentSubTask = subTasks.get(subTask.getUid());
        if (currentSubTask == null) {
            throw new NotFoundException("Can't get subTask: " + subTask.getUid() + " Not found.");
        }
        checkIntersectionOfTasksTimes(subTask);
        prioritisedTasks.remove(subTask);

        currentSubTask.setName(subTask.getName());
        currentSubTask.setStatus(subTask.getStatus());
        currentSubTask.setDescription(subTask.getDescription());
        prioritisedTasks.add(currentSubTask);
        calculateStatus(subTask.getEpicId());
    }

    @Override
    public void delSubTaskById(int id) {
        SubTask removedSubTask = subTasks.remove(id);
        int epicId = removedSubTask.getEpicId();
        Epic existEpic = epics.get(epicId);
        existEpic.getSubTasksUids().remove((Integer) id);
        history.remove(id);
        calculateStatus(epicId);
    }

    //Epics
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void delAllEpics() {
        for (Epic epic : getAllEpics()) {
            history.remove(epic.getUid());
        }
        subTasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);

        if (epic != null) {
            history.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setUid(genId());
        epics.put(epic.getUid(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getUid());
        if (currentEpic == null) {
            throw new NotFoundException("Can't get Epic: " + epic.getUid() + "Not found.");
        }
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
    }

    @Override
    public void delEpicById(int id) {
        for (Integer subTask : epics.get(id).getSubTasksUids()) {
            subTasks.remove(subTask);
        }
        epics.remove(id);
        history.remove(id);
    }

    @Override
    public List<SubTask> getListOfEpicSubTasks(int id) {
        List<Integer> epicSubTasksUids = epics.get(id).getSubTasksUids();
        List<SubTask> epicSubTasks = new ArrayList<>();

        for (Integer epicSubTasksUid : epicSubTasksUids) {
            epicSubTasks.add(subTasks.get(epicSubTasksUid));
        }
        return epicSubTasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getAll();
    }


    private int genId() {
        return ++seq;
    }

    protected void calculateStatus(int id) {
        Epic existedEpic = epics.get(id);
        List<Integer> epicSubTasksUids = existedEpic.getSubTasksUids();
        int newSubtasksCounter = 0;
        int doneSubtasksCounter = 0;
        String epicStatus;
        LocalDateTime start = LocalDateTime.MAX;
        Duration duration = Duration.ofMinutes(0);
        LocalDateTime end = LocalDateTime.MIN;

        if (subTasks.isEmpty()) {
            existedEpic.setStartTime(start);
            existedEpic.setDuration(duration);
            existedEpic.setEndTime(end);
            existedEpic.setStatus(Status.NEW);
        } else {
            for (Integer epicSubTasksUid : epicSubTasksUids) {
//                startTime
                if (subTasks.get(epicSubTasksUid).getStartTime().isBefore(start)) {
                    start = subTasks.get(epicSubTasksUid).getStartTime();
                }
//                duration
                duration = duration.plus(subTasks.get(epicSubTasksUid).getDuration());
//                endTime
                if (subTasks.get(epicSubTasksUid).getEndTime().isAfter(end)) {
                    end = subTasks.get(epicSubTasksUid).getEndTime();
                }
//                status
                final Status status = subTasks.get(epicSubTasksUid).getStatus();
                if (status.equals(Status.NEW)) {
                    newSubtasksCounter++;
                } else if (status.equals(Status.IN_PROGRESS)) {
                    existedEpic.setStatus(Status.IN_PROGRESS);
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
            existedEpic.setStartTime(start);
            existedEpic.setDuration(duration);
            existedEpic.setEndTime(end);
            existedEpic.setStatus(Status.valueOf(epicStatus));
        }
    }

    private void checkIntersectionOfTasksTimes(Task task) {
        for (Task pTask : prioritisedTasks) {
            if (pTask.getUid() == task.getUid()) {
                continue;
            }

            if ((task.getStartTime().isAfter(pTask.getStartTime()) && task.getStartTime().isBefore(pTask.getEndTime()))
                    || task.getEndTime().isAfter(pTask.getStartTime()) && task.getEndTime().isBefore(pTask.getEndTime())) {
                throw new ValidationException("Есть пересечение с: " + pTask.getName() + ", ID: " + pTask.getUid());
            }
        }
    }

    public List<Task> getPrioritised() {
        return new ArrayList<>(prioritisedTasks);
    }
}
