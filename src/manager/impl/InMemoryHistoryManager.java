package manager.impl;

import manager.api.HistoryManager;
import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final Map<Long, Node> nodeMap = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();

        Node node = first;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linklast(task);
    }

    @Override
    public void remove(long id) {
        Node node = nodeMap.remove(id);

        if (node == null) {
            return;
        }

        removeNode(node);
        System.out.println("Task deleted from history by ID = " + id);
    }

    private void removeNode(Node node) {
        if (node.prev == null) {
            first = node.next;
            if (first == null) {
                last = null;
            } else {
                first.prev = null;
            }
        } else {
            node.prev.next = node.next;
            if (node.next == null) {
                last = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        }
    }

    private void linklast(Task task) {
        Node node = new Node(task, last, null);

        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        nodeMap.put(task.getId(), node);
        System.out.println("Task added to history by ID = " + task.getId());
    }

    static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}
