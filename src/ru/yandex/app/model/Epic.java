package ru.yandex.app.model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subTasksUids = new ArrayList<>();

    public Epic(String name) {
        super(name);
    }

    public ArrayList<Integer> getSubTasksUids() {
        return subTasksUids;
    }

}