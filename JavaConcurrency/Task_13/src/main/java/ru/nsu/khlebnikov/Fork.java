package ru.nsu.khlebnikov;

import java.util.concurrent.locks.ReentrantLock;

public class Fork {
    private final int id;
    private final ReentrantLock lock;

    public Fork(int id) {
        this.id = id;
        this.lock = new ReentrantLock();
    }

    public int getId() {
        return id;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public boolean isLocked() {
        return lock.isLocked();
    }
}