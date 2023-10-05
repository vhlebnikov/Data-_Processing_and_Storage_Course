package ru.nsu.khlebnikov;

import java.util.Scanner;
import java.util.concurrent.FutureTask;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SyncLinkedList<String> syncLinkedList = new SyncLinkedList<>();

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
