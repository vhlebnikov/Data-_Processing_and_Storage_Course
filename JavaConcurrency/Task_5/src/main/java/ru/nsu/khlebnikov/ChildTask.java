package ru.nsu.khlebnikov;

public class ChildTask implements Runnable {

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Some text");
        }
        System.out.println("Child thread has been interrupted");
    }
}