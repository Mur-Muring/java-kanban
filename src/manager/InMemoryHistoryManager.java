/*
1. Убрала параметризацию в Node
2. Лишний пробел устранен
3. Убрала геттеры у Node
4. Метод add(Task task) вынесла общее добавлению в мапу, проверку на null НЕ убрала, так как в прошлый раз
просили добавить в тест проверку на ноль, потому что задача и пустая может быть
5. В метод removeNode() сделала проверку на null
6. Сделала тесты добвления, обхода, удаления приватными
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

    private static class Node<Task> {
        public Task task;
        public Node<Task> prev;
        public Node<Task> next;

        public Node(Node<Task> prev, Task task, Node<Task> next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
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
            } else {
                linkLast(task);
            }
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
        Node<Task> l = this.tail;
        Node<Task> newNode = new Node<>(l, task, null);
        this.tail = newNode;
        if (l == null) {
            this.head = newNode;
        } else {
            l.next = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
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
}
