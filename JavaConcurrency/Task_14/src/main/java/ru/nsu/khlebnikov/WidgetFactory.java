package ru.nsu.khlebnikov;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class WidgetFactory implements Callable<Void> {
    private final Semaphore detailsC;
    private final Semaphore modules;


    public WidgetFactory(Semaphore detailsC, Semaphore modules) {
        this.detailsC = detailsC;
        this.modules = modules;
    }


    @Override
    public Void call() {
        for (int i = 1; true; i++) {
            try {
                detailsC.acquire();
                modules.acquire();
            } catch (InterruptedException e) {
                System.out.println("WidgetFactory has been interrupted");
                return null;
            }
            System.out.println("№." + i + " Widget has been developed");
        }
    }
}
