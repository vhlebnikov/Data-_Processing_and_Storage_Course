package ru.nsu.khlebnikov;

public class Main {
    public static void main(String[] args) {
        int numberOfThreads;

        if (args.length != 1) {
            System.out.println("Arguments must contain number of threads!");
            return;
        }

        try {
            numberOfThreads = Integer.parseInt(args[0]);
            WorkersFactory workersFactory = new WorkersFactory(numberOfThreads);
            Runtime.getRuntime().addShutdownHook(new Thread(workersFactory::stop));
            workersFactory.calculate();
        } catch (NumberFormatException e) {
            System.out.println("Argument must be a number");
        }
    }
}