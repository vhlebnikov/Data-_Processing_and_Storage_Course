package ru.nsu.khlebnikov.current;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ChildTask implements Runnable {
    private final ReentrantLock lock = Main.lock;
    private final Condition condition = Main.condition;

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                lock.lock();
                while (Main.mainTurn) {
                    condition.await();
                }
                System.out.println(i + " message from child thread");
                Main.mainTurn = true;
                condition.signalAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}
