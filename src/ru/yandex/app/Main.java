package ru.yandex.app;

import ru.yandex.app.model.Epic;
import ru.yandex.app.model.Status;
import ru.yandex.app.model.SubTask;
import ru.yandex.app.model.Task;
import ru.yandex.app.service.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
       // InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        TaskManager taskManager = Managers.getDefaultTaskManager();

//      ---Tasks---
        Task task1 = taskManager.createTask(new Task("New Task 1", "", Status.NEW));
        System.out.println("Created task: " + task1);
        Task task2 = taskManager.createTask(new Task("New Task 2", "", Status.NEW));
        System.out.println("Created task: " + task2);

        Task task3 = taskManager.createTask(new Task("New Task 3", "", Status.NEW));
        Task taskFromManager3 = taskManager.getTaskById(task3.getUid());

        Task task4 = taskManager.createTask(new Task("New Task 4", "", Status.NEW));
        Task taskFromManager4 = taskManager.getTaskById(task4.getUid());

        Task task5 = taskManager.createTask(new Task("New Task 5", "", Status.NEW));
        Task taskFromManager5 = taskManager.getTaskById(task5.getUid());

        Task task6 = taskManager.createTask(new Task("New Task 6", "", Status.NEW));
        Task taskFromManager6 = taskManager.getTaskById(task6.getUid());

        Task task7 = taskManager.createTask(new Task("New Task 7", "", Status.NEW));
        Task taskFromManager7 = taskManager.getTaskById(task7.getUid());
        Task task8 = taskManager.createTask(new Task("New Task 8", "", Status.NEW));
        Task taskFromManager8 = taskManager.getTaskById(task8.getUid());
        Task task9 = taskManager.createTask(new Task("New Task 9", "", Status.NEW));
        Task taskFromManager9 = taskManager.getTaskById(task9.getUid());
        Task task10 = taskManager.createTask(new Task("New Task 10", "", Status.NEW));
        Task taskFromManager10 = taskManager.getTaskById(task10.getUid());
        Task task11 = taskManager.createTask(new Task("New Task 11", "", Status.NEW));
        Task taskFromManager11 = taskManager.getTaskById(task11.getUid());
        Task task12 = taskManager.createTask(new Task("New Task 12", "", Status.NEW));
        Task taskFromManager12 = taskManager.getTaskById(task12.getUid());


        Task taskFromManager1 = taskManager.getTaskById(task1.getUid());
        System.out.println("\nGet task by ID: " + taskFromManager1);
        Task taskFromManager2 = taskManager.getTaskById(task2.getUid());
        System.out.println("Get task by ID: " + taskFromManager2);

        System.out.println("\nViews History: " + taskManager.getHistory());

        taskFromManager1.setName("DEV Task 1");
        taskFromManager1.setDescription("Solo Task");
        taskFromManager1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(taskFromManager1);
        taskFromManager1 = taskManager.getTaskById(task1.getUid());
        System.out.println("\nUpdated task: " + taskFromManager1);

        taskManager.delTaskById(taskFromManager1.getUid());
        System.out.println("\nDeleted task: " + taskFromManager1);
        taskFromManager1 = taskManager.getTaskById(task1.getUid());
        System.out.println("Get task by ID: " + taskFromManager1);
//        Get All Tasks
        List<Task> allTasks = taskManager.getAllTasks();
        System.out.println("\nAll Tasks : " + allTasks);

        System.out.println("\nViews History: " + taskManager.getHistory());


//        Delete All Tasks
//        taskManager.delAllTasks();

//        ---Epics---
//        create Epic
        Epic epic1 = taskManager.createEpic(new Epic("New Epic 1"));
        System.out.println("\n\n\nCreated Epic: " + epic1);
        Epic epic2 = taskManager.createEpic(new Epic("New Epic 2"));
        System.out.println("Created Epic: " + epic2);

        Epic epicFromManager1 = taskManager.getEpicById(epic1.getUid());
        System.out.println("\nGet Epic by ID: " + epicFromManager1);

        taskFromManager10 = taskManager.getTaskById(task10.getUid());
        taskFromManager3 = taskManager.getTaskById(task3.getUid());

        System.out.println("\nViews History: " + taskManager.getHistory());

////        Get all Epics
//        List<Epic> allEpics = inMemoryTaskManager.getAllEpics();
//        System.out.println("\nAll Epics: " + allEpics);
//        //        Create SubTask
//        SubTask subTask1 = inMemoryTaskManager.createSubTask(new SubTask("New SubTask 1", "department 1", "NEW", epic1.getUid()));
//        System.out.println("\nCreated SubTask: " + subTask1);
//        SubTask subTask2 = inMemoryTaskManager.createSubTask(new SubTask("New SubTask 2", "department 2", "NEW", epic1.getUid()));
//        System.out.println("Created SubTask: " + subTask2);
//        SubTask subTask3 = inMemoryTaskManager.createSubTask(new SubTask("New SubTask 3", "department 3", "NEW", epic2.getUid()));
//        System.out.println("Created SubTask: " + subTask3);
//        //        Get list of Epic's Subtasks
//        ArrayList<SubTask> listOfEpicSubTasks = inMemoryTaskManager.getListOfEpicSubTasks(epic1.getUid());
//        System.out.println("\nList of Epic SubTasks: " + listOfEpicSubTasks);
//        //        Get All subTasks
//        ArrayList<SubTask> allSubTasks = inMemoryTaskManager.getAllSubTasks();
//        System.out.println("\nList of All SubTasks: " + allSubTasks);
//        //        Update subTask
//        subTask2.setName("New SubTask 2A");
//        subTask2.setDescription("department 4");
//        subTask2.setStatus("DONE"); //IN_PROGRESS
//        inMemoryTaskManager.updateSubTask(subTask2);
//
//        subTask3.setStatus("DONE");
//        inMemoryTaskManager.updateSubTask(subTask3);
//        allSubTasks = inMemoryTaskManager.getAllSubTasks();
//        System.out.println("\nList of All SubTasks: " + allSubTasks);
//
//        allEpics = inMemoryTaskManager.getAllEpics();
//        System.out.println("\nAll Epics: " + allEpics);
//
////      delete Epic By Id
//        inMemoryTaskManager.delEpicById(epic2.getUid());
//        allEpics = inMemoryTaskManager.getAllEpics();
//        System.out.println("All Epics: " + allEpics);
//        allSubTasks = inMemoryTaskManager.getAllSubTasks();
//        System.out.println("\nList of All SubTasks: " + allSubTasks);
////        delete SubTask By Id
//        inMemoryTaskManager.delSubTaskById(subTask2.getUid());
//        allSubTasks = inMemoryTaskManager.getAllSubTasks();
//        System.out.println("\nList of All SubTasks: " + allSubTasks);
//        allEpics = inMemoryTaskManager.getAllEpics();
//        System.out.println("All Epics: " + allEpics);
//
//////      delete All Epics
////        taskManager.delAllEpics();
////        allEpics = taskManager.getAllEpics();
////        System.out.println("All Epics: " + allEpics);
////      update Epic
//        epic1.setDescription("Epic for 1 Department");
//        inMemoryTaskManager.updateEpic(epic1);
//        System.out.println("\nGet Epic by ID: " + epicFromManager1);

//        ---SubTasks---
//        //Delete All subTasks
//        taskManager.delAllSubTasks();

////      Get SubTask by ID
//        SubTask subTaskFromManager1 = taskManager.getSubTaskById(subTask1.getUid());
//        System.out.println("Get SubTask by ID: " + subTaskFromManager1);
    }
}
