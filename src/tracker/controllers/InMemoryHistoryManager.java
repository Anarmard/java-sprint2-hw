package tracker.controllers;
import tracker.model.Epic;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager{
    public List<Task> historyTaskList = new ArrayList<>();
    private DoublyLinkedList<Task> historyLinkedList = new DoublyLinkedList<>();
    private HashMap<Long, Node<Task>> historyMap = new HashMap<>();

    public void removeNode (Node node) {
        historyLinkedList.remove(node);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            // Теперь с помощью HashMap и метода удаления removeNode метод add(Task task)
            // будет быстро удалять задачу из списка, если она там есть

            if (historyMap.containsKey(task.getId())) {
                removeNode(historyMap.get(task.getId()));
                historyLinkedList.remove(task.getId());
            }

            // затем вставлять её в конец двусвязного списка
            historyLinkedList.add(task); // добавление элемента в LinkedList
            historyLinkedList.linkLast(task); // обновление полей LinkedList
            if (historyLinkedList.size() > 10) {
                historyLinkedList.removeFirst();
            }

            // не забудьте обновить значение узла в HashMap
            historyMap.put(task.getId(), historyLinkedList.getTail());
        }
    }

    @Override
    public void remove(Long id) {
        historyTaskList.remove(id);
    }

    // Реализация метода getHistory должна перекладывать задачи из связного списка в ArrayList для формирования ответа.
    @Override
    public List<Task> getHistory() {
        historyTaskList.addAll(historyLinkedList);
        return historyTaskList;
    }

}

class DoublyLinkedList <T> extends LinkedList {
    private Node<Task> head = null;
    private Node<Task> tail = null;
    private int size = 0;

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
    }

    public Node<Task> getTail() {
        return tail;
    }
}

