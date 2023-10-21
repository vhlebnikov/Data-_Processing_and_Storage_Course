package ru.nsu.khlebnikov;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class DetailFactory implements Callable<Void> {
    private final String name;
    private final Semaphore semaphore;
    private final long developmentTime;

    public DetailFactory(String name, Semaphore semaphore, long developmentTime) {
        this.name = name;
        this.semaphore = semaphore;
        this.developmentTime = developmentTime;
    }

    @Override
    public Void call() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(developmentTime);
            } catch (InterruptedException e) {
                System.out.println(name + " DetailFactory has been interrupted");
                return null;
            }
            semaphore.release();
            System.out.println(name + " detail has been developed");
        }
    }
}
