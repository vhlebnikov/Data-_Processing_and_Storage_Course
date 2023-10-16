package ru.nsu.khlebnikov.current;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static final ReentrantLock lock = new ReentrantLock();
    public static final Condition condition = lock.newCondition();
    public static boolean mainTurn = true;

    public static void main(String[] args) {
        new Thread(new ChildTask()).start();

        for (int i = 0; i < 10; i++) {
            try {
                lock.lock();
                while (!mainTurn) {
                    condition.await();
                }
                System.out.println(i + " message from main thread");
                mainTurn = false;
                condition.signalAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }
}
