package ru.nsu.khlebnikov;

public class Main {
    public static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread childThread = new Thread(new ChildTask());
        childThread.start();
        for (int i = 0; i < 10; i++){
            synchronized (lock) {
                System.out.println(i + " message from main thread");
                lock.wait();
                lock.notify();
            }
        }
    }
}