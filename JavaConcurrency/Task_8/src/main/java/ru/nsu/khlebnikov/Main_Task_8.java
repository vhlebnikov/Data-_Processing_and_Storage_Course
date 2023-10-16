package ru.nsu.khlebnikov;

public class Main_Task_8 {
    public static void main(String[] args) {
        int numberOfThreads;

        if (args.length != 1) {
            System.out.println("Arguments must contain number of threads!");
            return;
        }

        try {
            numberOfThreads = Integer.parseInt(args[0]);
            WorkersFactory_Task_8 workersFactory = new WorkersFactory_Task_8(numberOfThreads);
            Runtime.getRuntime().addShutdownHook(new Thread(workersFactory::stop));
            workersFactory.calculate();
        } catch (NumberFormatException e) {
            System.out.println("Argument must be a number");
        }
    }
}