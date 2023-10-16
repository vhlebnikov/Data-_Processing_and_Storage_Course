package ru.nsu.khlebnikov;

public class MainTask implements Runnable {
    private final Object mainLock = Main.mainLock;
    private final Object childLock = Main.childLock;

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (mainLock) {
                System.out.println(i + " message from main thread");
                synchronized (childLock) {
                    childLock.notify();
                }
                try {
                    mainLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        synchronized (childLock) {
            childLock.notify();
        }
    }
}