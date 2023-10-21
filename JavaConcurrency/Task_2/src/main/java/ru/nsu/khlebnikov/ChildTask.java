package ru.nsu.khlebnikov;

public class ChildTask implements Runnable{

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++){
            System.out.println("Message " + i + " from child thread");
        }
    }
}