package ru.nsu.khlebnikov;

public class Main {
    public static void main(String[] args) {
        Thread childThread = new Thread(new ChildTask());
        childThread.start();
        for (int i = 0; i < 10; i++){
            System.out.println("Message from main thread");
        }
    }
}
