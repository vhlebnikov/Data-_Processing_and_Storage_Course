package ru.nsu.khlebnikov;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads, numberOfIterations;
        if (args.length != 2) {
            System.out.println("Number of arguments must equals 2: number of threads and number of iterations!");
            return;
        }
        numberOfThreads = Integer.parseInt(args[0]);
        numberOfIterations = Integer.parseInt(args[1]);

        WorkersFactory workersFactory = new WorkersFactory(numberOfThreads, numberOfIterations);
        System.out.println(workersFactory.calculate());
    }
}