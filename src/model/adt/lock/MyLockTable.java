package model.adt.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyLockTable implements ILockTable {
    private Map<Integer, Integer> lockTable;
    private int freeLocation;
    private final Lock lock;

    public MyLockTable() {
        this.lockTable = new HashMap<>();
        this.freeLocation = 1;
        this.lock = new ReentrantLock();
    }

    @Override
    public synchronized int allocate() {
        lock.lock();
        try {
            while (lockTable.containsKey(freeLocation)) {
                freeLocation++;
            }
            int location = freeLocation;
            lockTable.put(location, -1);
            freeLocation++;
            return location;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized int get(int location) {
        lock.lock();
        try {
            return lockTable.getOrDefault(location, -1);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void update(int location, int value) {
        lock.lock();
        try {
            lockTable.put(location, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized boolean contains(int location) {
        lock.lock();
        try {
            return lockTable.containsKey(location);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized Map<Integer, Integer> getContent() {
        lock.lock();
        try {
            return new HashMap<>(lockTable);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void setContent(Map<Integer, Integer> content) {
        lock.lock();
        try {
            this.lockTable = new HashMap<>(content);
            int maxLocation = lockTable.keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            this.freeLocation = maxLocation + 1;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return lockTable.toString();
        } finally {
            lock.unlock();
        }
    }
}
