package ru.nsu.khlebnikov;

import java.util.Date;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread childThread = new Thread(new ChildTask());
        childThread.start();
        Date start = new Date();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.err.println("Main thread has been interrupted");
        }
        childThread.interrupt();
        childThread.join();
        System.out.println("Start = " + start + "\nFinish = " + new Date());
    }
}
