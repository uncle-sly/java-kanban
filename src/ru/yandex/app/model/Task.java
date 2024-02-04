package ru.yandex.app.model;

public class Task {
    private int uid;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name) {
        this.name = name;
        this.status = Status.NEW;
    }

    public Task() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        return uid == task.uid;
    }

    @Override
    public int hashCode() {
        return uid;
    }

    @Override
    public String toString() {
        return "\nTask{" +
               "id=" + uid +
               ", name='" + name + '\'' +
               ", status='" + status+ '\'' +
               ", description='" + description + '\'' +
               "}\n";
    }

}
