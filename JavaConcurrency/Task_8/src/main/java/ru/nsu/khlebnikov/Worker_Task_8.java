package ru.nsu.khlebnikov;

/**
 * Class for calculating partial sum of the Leibniz sequence.
 */
public class Worker_Task_8 implements Runnable {
    private double from;
    private final double to;
    private double result;
    private int iterations;

    public Worker_Task_8(int from, int to) {
        this.from = from;
        this.to = to;
        this.result = 0;
        this.iterations = 0;
    }

    @Override
    public void run() {
        for (; (from < to) && !WorkersFactory_Task_8.isStopFlag(); from++) {
            if (from % 2 == 0) {
                result += 1 / (2 * from + 1);
            } else {
                result -= 1 / (2 * from + 1);
            }
            iterations++;
        }

        if (WorkersFactory_Task_8.isStopFlag()) {
            finishWork();
        } else {
            WorkersFactory_Task_8.addToResult(result * 4);
            WorkersFactory_Task_8.setMaxIterations(iterations);
        }
    }

    private void finishWork() {
        WorkersFactory_Task_8.setMaxIterations(iterations);
        int maxIterations = WorkersFactory_Task_8.getMaxIterations();
        while ((iterations < maxIterations) && (from < to)) {
            if (from % 2 == 0) {
                result += 1 / (2 * from + 1);
            } else {
                result -= 1 / (2 * from + 1);
            }
            iterations++;
            from++;
        }
        WorkersFactory_Task_8.addToResult(result * 4);
    }
}