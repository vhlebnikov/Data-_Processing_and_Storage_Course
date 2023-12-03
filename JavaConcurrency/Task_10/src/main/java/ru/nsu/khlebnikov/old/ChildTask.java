package ru.nsu.khlebnikov.old;

public class ChildTask implements Runnable {
    private final Object mainLock = Main.mainLock;
    private final Object childLock = Main.childLock;

    @Override
    public void run() {
        synchronized (childLock) {
            try {
                childLock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < 10; i++) {
            synchronized (childLock) {
                System.out.println(i + " message from child thread");
                synchronized (mainLock) {
                    mainLock.notify();
                }
                try {
                    childLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}