package tracker.controllers;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private HashMap<Long, Node> historyMap = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;
    private int size = 0;

    // удаляем узел Node из двусвязного списка
    public void removeNode (Node node) {
        if (node == head && node == tail) { // когда в двусвязном списке только один элемент. Он же голова, он же хвост
            head = null;
            tail = null;
        } else if (node == head) { // когда в двусвязном списке удаляемый узел - голова
            Node<Task> nextNode = node.next;
            head = nextNode;
            nextNode.prev = null;
        } else if (node == tail) { // когда в двусвязном списке удаляемый узел - хвост
            Node <Task> prevNode = node.prev;
            tail = prevNode;
            prevNode.next = null;
        } else {
            Node<Task> prevNode = node.prev;
            Node<Task> nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    // C помощью HashMap и метода удаления removeNode метод add(Task task) будет быстро удалять Node
    // из двусвязного списка, если он там есть
    @Override
    public void add(Task task) {
        if (task != null) {
            Long idMap = task.getId();

            // проверяем есть ли в Map уже такой id. Если да, то удаляем вырезаем Node из списка
            if (historyMap.containsKey(idMap)) {
                removeNode(historyMap.get(idMap));
            }

            // затем добавляем задачу в конец двусвязного списка
            linkLast(task); // обновление полей LinkedList

            // не забудьте обновить значение узла в HashMap
            Node<Task> nodeMap = tail;
            historyMap.put(idMap, nodeMap);
        }
    }

    @Override
    public void remove(Long id) {
        removeNode(historyMap.get(id));
    }

    // Реализация метода getHistory должна перекладывать задачи из связного списка в ArrayList для формирования ответа.
    @Override
    public List<Task> getHistory() {
        List<Task> historyTaskList = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            historyTaskList.add(node.getData());
            node = node.next;
        }
        return historyTaskList;
    }

    // узел Node добавляем в конец двусвязного списка
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
}
