/*
1. Убрала лишнее из add(Task task)
 */

package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> tasksHistory = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {
        public Task task;
        public Node prev;
        public Node next;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(task, node.task) && Objects.equals(prev, node.prev) && Objects.equals(next, node.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task, prev, next);
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            int idKey = task.getIdTask();
            remove(idKey);
            linkLast(task);
            tasksHistory.put(idKey, tail);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(tasksHistory.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node l = this.tail;
        Node newNode = new Node(l, task, null);
        this.tail = newNode;
        if (l == null) {
            this.head = newNode;
        } else {
            l.next = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            final Node prev = node.prev;
            final Node next = node.next;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
        }
    }
}
