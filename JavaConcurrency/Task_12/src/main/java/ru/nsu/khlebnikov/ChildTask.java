package ru.nsu.khlebnikov;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ChildTask implements Callable<Void> {
    SyncLinkedList syncLinkedList;

    public ChildTask(SyncLinkedList syncLinkedList) {
        this.syncLinkedList = syncLinkedList;
    }

    @Override
    public Void call() throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(5);
            syncLinkedList.sort();
        }
    }
}
