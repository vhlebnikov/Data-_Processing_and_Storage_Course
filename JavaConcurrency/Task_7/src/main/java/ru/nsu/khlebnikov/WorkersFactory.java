package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class that represents factory for distribution work of calculating partial sums
 * between provided thread and obtaining the final result (pi value).
 */
public class WorkersFactory {
    private final ExecutorService executorService;
    List<Worker> workers = new ArrayList<>();
    List<Future<Double>> workersResults = new ArrayList<>();
    private final int numberOfIterations;
    private final int numberOfThreads;

    public WorkersFactory(int numberOfThreads) {
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.numberOfIterations = 1000000000;
        this.numberOfThreads = numberOfThreads;
    }

    /**
     * Calculate pi value for "numberOfIterations" iterations.
     *
     * @return pi value.
     * @throws InterruptedException if interrupted exception occurred
     */
    public double calculate() throws InterruptedException {
        int div = numberOfIterations / numberOfThreads;
        int mod = numberOfIterations % numberOfThreads;

        int start = 0;
        int end;

        for (int i = 0; i < numberOfThreads; i++) {
            end = mod-- > 0 ? start + div + 1 : start + div;
            workers.add(new Worker(start, end));
            start = end;
        }

        workersResults = executorService.invokeAll(workers);
        executorService.shutdown();

        return workersResults.stream().map(x -> {
            try {
                return x.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).reduce(0d, Double::sum);
    }
}
