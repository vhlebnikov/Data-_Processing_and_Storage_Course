package ru.nsu.khlebnikov.current;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static final ReentrantLock lock = new ReentrantLock();
    public static final Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        new Thread(new ChildTask()).start();

        for (int i = 0; i < 10; i++) {
            lock.lock();
            System.out.println(i + " main");
            condition.signalAll();
            condition.await();
            lock.unlock();
        }
    }
}
