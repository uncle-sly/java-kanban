package ru.yandex.app.converter;

import ru.yandex.app.model.Task;

public class TaskToStringConverter {

    public static String taskToString(Task task) {
        return task.getUid() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                "," + task.getDescription() + "," + task.getEpicId();
    }
}
