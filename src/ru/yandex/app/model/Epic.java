package ru.yandex.app.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksUids;

    public Epic(String name) {
        super(name);
        this.subTasksUids = new ArrayList<>();
    }

    public Epic(Integer uid, String name) {
        super(name);
        this.setUid(uid);
        this.subTasksUids = new ArrayList<>();
    }

    public List<Integer> getSubTasksUids() {
        return subTasksUids;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}