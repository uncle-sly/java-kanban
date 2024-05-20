package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.prev = prev;
            this.next = next;
        }
    }

    HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {
        Node node = history.get(task.getUid());

        if (node != null) {
            removeNode(node);
            linkLast(task);

        } else {
            linkLast(task);
        }
    }

    @Override
    public List<Task> getAll() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.item);
            current = current.next;
        }
        return list;
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    // добавить задачу в связ список, связать одну ноду с другой
    private void linkLast(Task task) {
        final Node lastNode = last;
        final Node newNode = new Node(lastNode, task, null);

        last = newNode;
        if (lastNode == null) {
            first = newNode;
            history.put(task.getUid(), newNode);
        } else {
            lastNode.next = newNode;
            history.put(task.getUid(), newNode);
        }

    }

    private void removeNode(Node node) {

        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode == null) {
            first = nextNode;
            nextNode.prev = null;
            history.remove(node.item.getUid());

        } else if (nextNode == null) {
            last = prevNode;
            prevNode.next = null;
            history.remove(node.item.getUid());
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            history.remove(node.item.getUid());
        }
    }
}

