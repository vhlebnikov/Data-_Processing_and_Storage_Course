package ru.nsu.khlebnikov;

import java.util.concurrent.Callable;

/**
 * Class for calculating partial sum of the Leibniz sequence.
 */
public class Worker implements Callable<Double> {
    private final int from;
    private final int to;

    public Worker(int from, int to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Calculate partial sum of the Leibniz sequence from "from" inclusive to "to" exclusive.
     *
     * @return partial sum.
     */
    @Override
    public Double call() {
        double result = 0;
        for (double i = from; i < to; i++) {
            if (i % 2 == 0) {
                result += 1 / (2 * i + 1);
            } else {
                result -= 1 / (2 * i + 1);
            }
        }
        System.err.println(Thread.currentThread().getId() + " result " + result * 4);
        return result * 4;
    }
}
