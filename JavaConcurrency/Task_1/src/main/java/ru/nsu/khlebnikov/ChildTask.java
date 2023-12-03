package ru.nsu.khlebnikov;

public class ChildTask implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 10; i++){
            System.out.println("Message from child thread");
        }
    }
}
