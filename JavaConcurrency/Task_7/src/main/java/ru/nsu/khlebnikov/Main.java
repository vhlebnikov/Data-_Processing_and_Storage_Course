package ru.nsu.khlebnikov;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads;
        if (args.length != 1) {
            System.out.println("Arguments must contain number of threads!");
            return;
        }
        numberOfThreads = Integer.parseInt(args[0]);

        WorkersFactory workersFactory = new WorkersFactory(numberOfThreads);
        System.out.println(workersFactory.calculate());
    }
}