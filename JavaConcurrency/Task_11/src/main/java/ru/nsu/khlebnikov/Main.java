package ru.nsu.khlebnikov;

import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

public class Main {
    public static volatile Semaphore semaphore1 = new Semaphore(1);
    public static volatile Semaphore semaphore2 = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        Thread childThread = new Thread(new FutureTask<>(new ChildTask()));
        childThread.start();
        for (int i = 0; i < 10; i++) {
            semaphore1.acquire();
            System.out.println(i + " message from main thread");
            semaphore2.release();
        }
    }
}