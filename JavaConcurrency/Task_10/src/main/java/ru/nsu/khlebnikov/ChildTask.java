package ru.nsu.khlebnikov;

public class ChildTask implements Runnable {
    final Object lock = Main.lock;

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (lock) {
                System.out.println(i + " message from child thread");
                lock.notify();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}