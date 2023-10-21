package ru.nsu.khlebnikov;

import java.util.Scanner;
import java.util.concurrent.FutureTask;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            SyncLinkedList syncLinkedList = new SyncLinkedList();

            new Thread(new FutureTask<>(new ChildTask(syncLinkedList))).start();

            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();

                if (input.isBlank()) {
                    syncLinkedList.print();
                } else {
                    syncLinkedList.addFirst(input);
                }
            }
        }
    }
}
