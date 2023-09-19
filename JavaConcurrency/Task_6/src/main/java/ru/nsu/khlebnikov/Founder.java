package ru.nsu.khlebnikov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public final class Founder {
    private final List<Runnable> workers;

    public Founder(final Company company) {
        int departmentCount = company.getDepartmentsCount();
        this.workers = new ArrayList<>(departmentCount);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(departmentCount, company::showCollaborativeResult);
        for (int i = 0; i < departmentCount; i++) {
            workers.add(new Worker(company.getFreeDepartment(i), cyclicBarrier));
        }
    }
    public void start() {
        for (final Runnable worker : workers) {
            new Thread(worker).start();
        }
    }
}