/*
-Сделать список хранения истории безразмерным
-Сделать класс Node, для узла списка, реализуем здесть, так как он больше нигде не нужен
-Реализовать метод linkLast для добавления задач в двухсвязный список
-Реализовать метод getTasks для того что бы собрать все задачи в обычный список
-Реализовать метод removeNode, удаление по Node
-Реализовать метод remove, удаление из Маp по id
-Реализовать метод добавления в Мар
 */

package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> tasksHistory = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;


    private static class Node<E> {
        public E task;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E task, Node<E> next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        public E getTask() {
            return task;
        }

        public Node<E> getNext() {
            return next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
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
            if (tasksHistory.containsKey(idKey)) {
                remove(idKey);
                linkLast(task);
                tasksHistory.put(idKey, tail);
            } else {
                linkLast(task);
                tasksHistory.put(idKey, tail);
            }
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

    public void linkLast(Task task) {
        Node<Task> l = this.tail;
        Node<Task> newNode = new Node<>(l, task, null);
        this.tail = newNode;
        if (l == null) {
            this.head = newNode;
        } else {
            l.next = newNode;
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.getTask());
            current = current.getNext();
        }
        return tasks;
    }

    public void removeNode(Node<Task> node) {
        final Node<Task> prev = node.prev;
        final Node<Task> next = node.next;
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
