package ru.nsu.khlebnikov;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher implements Runnable {
    private static final ReentrantLock forks = new ReentrantLock();
    private static final Condition condition = forks.newCondition();
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

            forks.lock();

            /* Если надо, чтобы он всё-таки одну вилку подбирал, а потом убирал,
               можно использовать tryLock.
            */
            while (left.isLocked() || right.isLocked()) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            left.lock();
            System.out.println(name + " взял левую вилку");
            right.lock();
            System.out.println(name + " взял правую вилку");

            forks.unlock();

            System.out.println(name + " принялся кушать");
            timeWasting();
            satietyLevel += Math.random();
            System.out.println(name + " покушал");

            forks.lock();

            right.unlock();
            System.out.println(name + " освободил правую вилку");
            left.unlock();
            System.out.println(name + " освободил левую вилку");
            condition.signalAll();

            forks.unlock();
        }
        System.out.println("=== " + name + " наелся и ушёл");
    }
}