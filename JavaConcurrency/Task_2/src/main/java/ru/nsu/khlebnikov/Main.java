package ru.nsu.khlebnikov;

public class Main {
    public static void main(String[] args) {
        Thread childThread = new Thread(new ChildTask());
        childThread.start();
        try {
            childThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        for (int i = 1; i <= 10; i++){
            System.out.println("Message " + i + " from main thread");
        }
    }
}