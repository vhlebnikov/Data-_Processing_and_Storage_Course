package ru.nsu.khlebnikov;

public class ChildTask implements Runnable {

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Some text");
        }
    }
}
