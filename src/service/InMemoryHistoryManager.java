// Сергей, добрый день! Код на проверку)

package service;

import data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Node<Task> node = nodeMap.get(task.getId());
        if (node != null) {
            removeNode(node);
        }

        Node<Task> newNode = linkLast(task);
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public ArrayList<Task> getHistory() { // new
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        return newNode;
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        nodeMap.remove(node.task.getId());
        node.prev = null;
        node.next = null;
        node.task = null;
    }

    private ArrayList<Task> getTasks(){
        ArrayList<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    private static class Node<T> {
        T task;
        Node<T> prev;
        Node<T> next;

        Node(T task) {
            this.task = task;
        }

        Node(Node<T> prev, T task, Node<T> next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }
}