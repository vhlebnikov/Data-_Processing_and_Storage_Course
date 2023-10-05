package ru.nsu.khlebnikov;

/**
 * Class for calculating partial sum of the Leibniz sequence.
 */
public class Worker implements Runnable {
    private final int from;
    private final int to;

    public Worker(int from, int to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Calculate partial sum of the Leibniz sequence from "from" inclusive to "to" exclusive.
     */
    @Override
    public void run() {
        double result = 0;
        for (double i = from; (i < to) && !WorkersFactory.isStopFlag(); i++) {
            if (i % 2 == 0) {
                result += 1 / (2 * i + 1);
            } else {
                result -= 1 / (2 * i + 1);
            }
        }
        WorkersFactory.addToResult(result * 4);
    }
}