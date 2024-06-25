package ru.yandex.app.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTasksUids = new ArrayList<>();

    public Epic(String name) {
        super(name);
    }

    public List<Integer> getSubTasksUids() {
        return subTasksUids;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}