package ru.yandex.app.service;

import ru.yandex.app.exceptions.ManagerSaveException;
import ru.yandex.app.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.yandex.app.converter.TaskToStringConverter.taskToString;


public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String FILE_CSV = "resources/file_tasks.csv";
    private static final String FILE_HEADER = "id,type,name,status,description,epic,duration,startTime";
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
    public void delAllTasks() {
        super.delAllTasks();
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

    @Override
    public void delTaskById(int id) {
        super.delTaskById(id);
    }

    //SubTasks
    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public void delAllSubTasks() {
        super.delAllSubTasks();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        return super.getSubTaskById(id);
    }

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

    @Override
    public void delSubTaskById(int id) {
        super.delSubTaskById(id);
    }

    //Epics
    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public void delAllEpics() {
        super.delAllEpics();
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

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

    @Override
    public void delEpicById(int id) {
        super.delEpicById(id);
    }

    @Override
    public List<SubTask> getListOfEpicSubTasks(int id) {
        return super.getListOfEpicSubTasks(id);
    }

    @Override
    public List<Task> getPrioritised() {
        return super.getPrioritised();
    }

    private Task fromString(String value) {
        final String[] columns = value.split(",");
        int uid = Integer.parseInt(columns[0]);
        TaskType type = TaskType.valueOf(columns[1]);
        String name = columns[2];
        String status = columns[3];
        String description = columns[4];
        String epicId = columns[5];
        Duration duration = Duration.parse(columns[6]);

        LocalDateTime startTime = LocalDateTime.MIN;
        if (!columns[7].equals("null")) {
            startTime = LocalDateTime.parse(columns[7]);
        }

        Task task = null;

        switch (type) {
            case TASK:
                task = new Task(name, description, Status.valueOf(status), duration, startTime);
                task.setUid(uid);
                break;
            case SUBTASK:
                task = new SubTask(name, description, Status.valueOf(status), duration, startTime, Integer.parseInt(epicId));
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
            writer.write(FILE_HEADER);
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
                TaskType type = task.getType();

                switch (type) {
                    case TASK:
                        tasks.put(uid, task);
                        prioritisedTasks.add(task);
                        break;
                    case EPIC:
                        epics.put(uid, (Epic) task);
                        break;
                    case SUBTASK:
                        subTasks.put(uid, (SubTask) task);
                        prioritisedTasks.add(task);
                        break;
                }

                if (maxUid < uid) {
                    maxUid = uid;
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        seq = maxUid;

        for (Epic epic : epics.values()) {
            calculateStatus(epic.getUid());
        }

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();

        return manager;
    }

}
