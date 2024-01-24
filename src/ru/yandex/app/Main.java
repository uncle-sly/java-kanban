package ru.yandex.app;

import ru.yandex.app.model.Epic;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.service.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = taskManager.createTask(new Task("New Task 1","","NEW"));
        System.out.println("Created task: " + task1);
        Task task2 = taskManager.createTask(new Task("New Task 2","","NEW"));
        System.out.println("Created task: " + task2);

        Task taskFromManager1 = taskManager.getTaskById(task1.getUid());
        System.out.println("\nGet task by ID: " + taskFromManager1);
        Task taskFromManager2 = taskManager.getTaskById(task1.getUid());
        System.out.println("Get task by ID: " + taskFromManager2);

        taskFromManager1.setName("New Dev Task 1");
        taskFromManager1.setDescription("Solo Task");
        taskFromManager1.setStatus("IN_PROGRESS");
        taskManager.updateTask(taskFromManager1);
        taskFromManager1 = taskManager.getTaskById(task1.getUid());
        System.out.println("\nUpdated task: " + taskFromManager1);

        taskManager.delTaskById(taskFromManager1.getUid());
        System.out.println("\nDeleted task: " + taskFromManager1);
        taskFromManager1 = taskManager.getTaskById(task1.getUid());
        System.out.println("Get task by ID: " + taskFromManager1);
//        Get All Tasks
        ArrayList<Task> allTasks = taskManager.getAllTasks();
        System.out.println("\nAll Tasks : " + allTasks);
////        Delete All Tasks
////        taskManager.delAllTasks();

//        Epics //
//        create Epic
        Epic epic1 = taskManager.createEpic(new Epic("New Epic 1"));
        System.out.println("\n\n\nCreated Epic: " + epic1);
        Epic epic2 = taskManager.createEpic(new Epic("New Epic 2"));
        System.out.println("Created Epic: " + epic2);

        Epic epicFromManager1 = taskManager.getEpicById(epic1.getUid());
        System.out.println("\nGet Epic by ID: " + epicFromManager1);
//        Get all Epics
        List<Epic> allEpics = taskManager.getAllEpics();
        System.out.println("\nAll Epics: " + allEpics);
        //        Create SubTask
        SubTask subTask1 = taskManager.createSubTask(new SubTask("New SubTask 1", "department 1" ,"NEW",epic1.getUid()));
        System.out.println("\nCreated SubTask: " + subTask1);
        SubTask subTask2 = taskManager.createSubTask(new SubTask("New SubTask 2", "department 2" ,"NEW",epic1.getUid()));
        System.out.println("Created SubTask: " + subTask2);
        SubTask subTask3 = taskManager.createSubTask(new SubTask("New SubTask 3", "department 3" ,"NEW",epic2.getUid()));
        System.out.println("Created SubTask: " + subTask3);
        //        Get list of Epic's Subtasks
        ArrayList<SubTask> listOfEpicSubTasks = taskManager.getListOfEpicSubTasks(epic1.getUid());
        System.out.println("\nList of Epic SubTasks: " + listOfEpicSubTasks);
        //        Get All subTasks
        ArrayList<SubTask> allSubTasks = taskManager.getAllSubTasks();
        System.out.println("\nList of All SubTasks: " + allSubTasks);
        //        Update subTask
        subTask2.setName("New SubTask 2A");
        subTask2.setDescription("department 4");
        subTask2.setStatus("IN_PROGRESS");
        taskManager.updateSubTask(subTask2);

        subTask3.setStatus("DONE");
        taskManager.updateSubTask(subTask3);
        allSubTasks = taskManager.getAllSubTasks();
        System.out.println("\nList of All SubTasks: " + allSubTasks);

        allEpics = taskManager.getAllEpics();
        System.out.println("\nAll Epics: " + allEpics);


//      delete Epic By Id
        taskManager.delEpicById(epic2.getUid());
        allEpics = taskManager.getAllEpics();
        System.out.println("All Epics: " + allEpics);
        allSubTasks = taskManager.getAllSubTasks();
        System.out.println("\nList of All SubTasks: " + allSubTasks);
//        delete SubTask By Id
        taskManager.delSubTaskById(subTask2.getUid());
        allSubTasks = taskManager.getAllSubTasks();
        System.out.println("\nList of All SubTasks: " + allSubTasks);
        allEpics = taskManager.getAllEpics();
        System.out.println("All Epics: " + allEpics);

////      delete All Epics
//        taskManager.delAllEpics();
//        allEpics = taskManager.getAllEpics();
//        System.out.println("All Epics: " + allEpics);
//      update Epic
        epic1.setDescription("Epic for 1 Department");
        taskManager.updateEpic(epic1);
        System.out.println("\nGet Epic by ID: " + epicFromManager1);
//        SubTasks //
//        Delete All subTasks
//        taskManager.delAllSubTasks();
////      Get SubTask by ID
//        SubTask subTaskFromManager1 = taskManager.getSubTaskById(subTask1.getUid());
//        System.out.println("Get SubTask by ID: " + subTaskFromManager1);

    }
}
