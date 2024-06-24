package ru.yandex.app.converter;

import ru.yandex.app.model.Task;

public class TaskToStringConverter {

    public static String taskToString(Task task) {
        String str_template = "%d,%s,%s,%s,%s,%d";
        return String.format(str_template, task.getUid(), task.getType(), task.getName(), task.getStatus(), task.getDescription(), task.getEpicId());
    }
}
