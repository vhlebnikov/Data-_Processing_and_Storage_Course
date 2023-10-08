package ru.nsu.khlebnikov;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class ModuleFactory implements Callable<Void> {
    private final Semaphore detailA;
    private final Semaphore detailB;
    private final Semaphore modules;

    public ModuleFactory(Semaphore detailA, Semaphore detailB, Semaphore modules) {
        this.detailA = detailA;
        this.detailB = detailB;
        this.modules = modules;
    }

    @Override
    public Void call() {
        while (true) {
            try {
                detailA.acquire();
                detailB.acquire();
            } catch (InterruptedException e) {
                System.out.println("ModuleFactory has been interrupted");
                return null;
            }
            modules.release();
            System.out.println("Module has been developed");
        }
    }
}
