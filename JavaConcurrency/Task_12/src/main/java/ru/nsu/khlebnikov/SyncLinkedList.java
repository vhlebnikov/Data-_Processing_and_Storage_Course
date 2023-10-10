package ru.nsu.khlebnikov;

import java.util.Comparator;
import java.util.LinkedList;

public class SyncLinkedList {
    final LinkedList<String> list = new LinkedList<>();

    public synchronized void addFirst(String value) {
        list.addFirst(value);
    }

    public synchronized void sort() {
        int length = list.size();
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (list.get(i).compareTo(list.get(j)) > 0) {
                    String temp = list.get(i);
                    list.set()
                }
            }
        }
    }

    public synchronized void print() {
        int index = 1;
        for (String s : list) {
            int length = s.length();
            System.out.print(index++ + ". ");
            for (int i = 0; i < length; i += 10) {
                System.out.println((i > 0 ? "   " : "") +
                        s.substring(i, (length - (i + 10) >= 0 ? i + 10 : i + length % 10)));
            }
        }
    }
}