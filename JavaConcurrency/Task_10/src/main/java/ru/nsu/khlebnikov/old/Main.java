package ru.nsu.khlebnikov.old;

public class Main {
    public static final Object mainLock = new Object();
    public static final Object childLock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = new Thread(new MainTask());
        Thread childThread = new Thread(new ChildTask());
        childThread.start();
        Thread.sleep(500);
        mainThread.start();
    }
}