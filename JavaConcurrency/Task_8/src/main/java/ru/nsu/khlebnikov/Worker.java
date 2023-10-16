package ru.nsu.khlebnikov;

/**
 * Class for calculating partial sum of the Leibniz sequence.
 */
public class Worker implements Runnable {
    private double from;
    private final double to;
    private double result;
    private int iterations;

    public Worker(int from, int to) {
        this.from = from;
        this.to = to;
        this.result = 0;
        this.iterations = 0;
    }

    @Override
    public void run() {
        for (; (from < to) && !WorkersFactory.isStopFlag(); from++) {
            if (from % 2 == 0) {
                result += 1 / (2 * from + 1);
            } else {
                result -= 1 / (2 * from + 1);
            }
            iterations++;
        }

        if (WorkersFactory.isStopFlag()) {
            finishWork();
        } else {
            WorkersFactory.addToResult(result * 4);
            WorkersFactory.setMaxIterations(iterations);
        }
    }

    private void finishWork() {
        WorkersFactory.setMaxIterations(iterations);
        int maxIterations = WorkersFactory.getMaxIterations();
        while ((iterations < maxIterations) && (from < to)) {
            if (from % 2 == 0) {
                result += 1 / (2 * from + 1);
            } else {
                result -= 1 / (2 * from + 1);
            }
            iterations++;
            from++;
        }
        WorkersFactory.addToResult(result * 4);
    }
}