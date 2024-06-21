package ru.yandex.app.service;

import ru.yandex.app.exceptions.ManagerSaveException;
import ru.yandex.app.model.*;

import java.io.*;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.yandex.app.converter.TaskToStringConverter.taskToString;


public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String FILE_CSV = "resources/file_tasks.csv";
    private final File file;

    public FileBackedTaskManager(HistoryManager history) {
        super(history);
        this.file = new File(FILE_CSV);
    }

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public FileBackedTaskManager(HistoryManager history, File file) {
        super(history);
        this.file = file;

    }

    // Tasks
    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    //SubTasks
    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    //Epics
    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    private Task fromString(String value) {
        final String[] columns = value.split(",");
        int uid = Integer.parseInt(columns[0]);
        TaskType type = TaskType.valueOf(columns[1]);
        String name = columns[2];
        String status = columns[3];
        String description = columns[4];
        String epicId = columns[5];

        Task task = null;

        switch (type) {
            case TASK:
                task = new Task(name, description, Status.valueOf(status));
                task.setUid(uid);
                break;
            case SUBTASK:
                task = new SubTask(name, description, Status.valueOf(status), Integer.parseInt(epicId));
                task.setUid(uid);
                break;
            case EPIC:
                task = new Epic(name);
                task.setUid(uid);
                break;
        }
        return task;
    }

    private void save() {

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(taskToString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(taskToString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                writer.append(taskToString(entry.getValue()));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void load() {
        int maxUid = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            reader.readLine();

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                final Task task = fromString(line);

                final int uid = task.getUid();
                if (task.getType() == TaskType.TASK) {
                    tasks.put(uid, task);
                } else if (task.getType() == TaskType.EPIC) {
                    epics.put(uid, (Epic) task);
                } else {
                    subTasks.put(uid, (SubTask) task);
                }

                if (maxUid < uid) {
                    maxUid = uid;
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        seq = maxUid;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();

        return manager;
    }

}
