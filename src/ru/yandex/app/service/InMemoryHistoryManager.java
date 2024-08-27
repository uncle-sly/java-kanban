package ru.yandex.app.service;

import ru.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.prev = prev;
            this.next = next;
        }
    }

    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        Node node = history.get(task.getUid());

        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
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
        if (node != null) {
            removeNode(node);
        }
    }

    // добавить задачу в связ список, связать одну ноду с другой
    private void linkLast(Task task) {
        final Node lastNode = last;
        final Node newNode = new Node(lastNode, task, null);

        last = newNode;
        if (lastNode == null) {
            first = newNode;
        } else {
            lastNode.next = newNode;
        }
        history.put(task.getUid(), newNode);
    }

    private void removeNode(Node node) {

        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode == null && nextNode == null) {
            history.remove(node.item.getUid());
            return;
        }

        if (prevNode == null) {
            first = nextNode;
            nextNode.prev = null;
        } else if (nextNode == null) {
            last = prevNode;
            prevNode.next = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
        node.next = null;
        node.prev = null;
        history.remove(node.item.getUid());
    }
}

