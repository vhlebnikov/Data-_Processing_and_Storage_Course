package ru.nsu.khlebnikov;

import java.util.Comparator;
import java.util.LinkedList;

public class SyncLinkedList<T> {
    final LinkedList<T> list = new LinkedList<>();

    public synchronized void addFirst(T value) {
        list.addFirst(value);
    }

    public synchronized void sort(Comparator<? super T> comparator) {
        list.sort(comparator);
    }

    public synchronized void print() {
        list.forEach(System.out::println);
    }
}