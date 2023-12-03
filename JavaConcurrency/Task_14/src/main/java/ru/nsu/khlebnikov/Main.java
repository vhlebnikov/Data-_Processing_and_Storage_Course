package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {
    private static long workingTime = 6;

    public static void main(String[] args) throws InterruptedException {
        Semaphore detailsA = new Semaphore(0);
        Semaphore detailsB = new Semaphore(0);
        Semaphore detailsC = new Semaphore(0);
        Semaphore modules = new Semaphore(0);

        List<Thread> taskList = new ArrayList<>();
        taskList.add(new Thread(new FutureTask<>(new DetailFactory("A", detailsA, 1))));
        taskList.add(new Thread(new FutureTask<>(new DetailFactory("B", detailsB, 2))));
        taskList.add(new Thread(new FutureTask<>(new DetailFactory("C", detailsC, 3))));
        taskList.add(new Thread(new FutureTask<>(new ModuleFactory(detailsA, detailsB, modules))));
        taskList.add(new Thread(new FutureTask<>(new WidgetFactory(detailsC, modules))));

        taskList.forEach(Thread::start);
        TimeUnit.SECONDS.sleep(workingTime);
        taskList.forEach(Thread::interrupt);
        TimeUnit.SECONDS.sleep(1);

        System.out.println("===================\nRemaining details:");
        System.out.println("A details: " + detailsA.availablePermits());
        System.out.println("B details: " + detailsB.availablePermits());
        System.out.println("C details: " + detailsC.availablePermits());
        System.out.println("Modules  : " + modules.availablePermits());
    }
}
