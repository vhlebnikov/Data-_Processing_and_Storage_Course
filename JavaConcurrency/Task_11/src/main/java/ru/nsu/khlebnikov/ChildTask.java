package ru.nsu.khlebnikov;

import java.util.concurrent.Callable;

public class ChildTask implements Callable<Void> {

    @Override
    public Void call() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Main.semaphore2.acquire();
            System.out.println(i + " message from child thread");
            Main.semaphore1.release();
        }
        return null;
    }
}