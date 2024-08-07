package ru.yandex.app.converter;

import ru.yandex.app.model.Task;

public class TaskToStringConverter {

    public static String taskToString(Task task) {
        String strTemplate = "%d,%s,%s,%s,%s,%s,%s,%s";
        return String.format(strTemplate, task.getUid(), task.getType(), task.getName(), task.getStatus(), task.getDescription(),
                task.getEpicId(), task.getDuration(), task.getStartTime());
    }
}
