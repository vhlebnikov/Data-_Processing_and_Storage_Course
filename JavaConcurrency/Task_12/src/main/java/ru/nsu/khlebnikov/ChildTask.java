package ru.nsu.khlebnikov;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ChildTask implements Callable<Void> {
    SyncLinkedList<String> syncLinkedList;

    public ChildTask(SyncLinkedList<String> syncLinkedList) {
        this.syncLinkedList = syncLinkedList;
    }

    @Override
    public Void call() throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(5);
            syncLinkedList.sort(String::compareTo);
        }
    }
}
