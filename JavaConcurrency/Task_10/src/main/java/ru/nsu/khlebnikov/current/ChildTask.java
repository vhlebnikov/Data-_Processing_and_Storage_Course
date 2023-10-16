package ru.nsu.khlebnikov.current;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ChildTask implements Runnable {
    private final ReentrantLock lock = Main.lock;
    private final Condition condition = Main.condition;

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(i + " child");
            condition.signalAll();
            lock.unlock();
        }
    }
}
