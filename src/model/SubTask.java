package model;

public class SubTask extends Task{

    Epic epic;

    public SubTask(String name, String description, String status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
