package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class that represents factory for distribution work of calculating partial sums
 * between provided thread and obtaining the final result (pi value).
 */
public class WorkersFactory {
    private final ExecutorService executorService;
    List<Worker> workers = new ArrayList<>();
    private final int numberOfIterations;
    private final int numberOfThreads;
    private static volatile double result;
    private static volatile boolean stopFlag;
    private static volatile int maxIterations;
    private static CountDownLatch countDownLatch;

    public WorkersFactory(int numberOfThreads) {
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.numberOfIterations = 1000000000;
        this.numberOfThreads = numberOfThreads;
        result = 0;
        stopFlag = false;
        maxIterations = 0;
        countDownLatch = new CountDownLatch(numberOfThreads);
    }

    public static boolean isStopFlag() {
        return stopFlag;
    }

    public static synchronized void addToResult(double value) {
        result += value;
    }

    public void calculate() {
        int div = numberOfIterations / numberOfThreads;
        int mod = numberOfIterations % numberOfThreads;

        int start = 0;
        int end;

        for (int i = 0; i < numberOfThreads; i++) {
            end = mod-- > 0 ? start + div + 1 : start + div;
            workers.add(new Worker(start, end));
            start = end;
        }

        workers.forEach(executorService::submit);
        executorService.shutdown();
    }

    public void stop() {
        if (maxIterations == 0) {
            System.out.println("Interrupted");
        } else {
            System.out.println("Successfully finished");
        }

        stopFlag = true;
        
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Result = " + result);
        System.out.println("Actual = " + Math.PI);
        System.out.println("Diff   = " + Math.abs(result - Math.PI));

    }

    public static synchronized void setMaxIterations(int iterations) {
        if (maxIterations < iterations) {
            maxIterations = iterations;
        }
    }

    public static int getMaxIterations() {
        countDownLatch.countDown();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return maxIterations;
    }
}