package ru.nsu.khlebnikov;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher implements Runnable {
    private static final Object forks = new Object();
    private final Fork left;
    private final Fork right;
    private final String name;
    private double satietyLevel;

    public Philosopher(Fork left, Fork right, String name) {
        this.left = left;
        this.right = right;
        this.name = name;
        this.satietyLevel = 0;
    }

    public void timeWasting() {
        try {
            TimeUnit.SECONDS.sleep(Double.valueOf(Math.random() * 3).longValue());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (satietyLevel < 1) {
            System.out.println(name + " размышляет");
            timeWasting();
            if (left.getLock().tryLock()) {
                left.getLock().lock();
                System.out.println(name + " взял левую вилку");
                synchronized (forks) {
                    if (right.getLock().tryLock()) {
                        right.getLock().lock();
                        System.out.println(name + " взял правую вилку");
                        System.out.println(name + " принялся кушать");
                        timeWasting();
                        satietyLevel += Math.random();
                        System.out.println(name + " покушал");
                        right.getLock().unlock();
                        System.out.println(name + " освободил правую вилку");
                    } else {
                        left.getLock().unlock();
                        System.out.println(name + " освободил левую вилку");
                    }
                }
                left.getLock().unlock();
                System.out.println(name + " освободил левую вилку");
            }
        }
        System.out.println("=== " + name + " наелся и ушёл");
    }
}