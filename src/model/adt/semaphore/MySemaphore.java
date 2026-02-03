package model.adt.semaphore;

import javafx.util.Pair;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphore implements ISemaphore {
    private Map<Integer, Pair<Integer, List<Integer>>> semaphoreTable;
    private int freeLocation = 1;
    private final Lock lock = new ReentrantLock();

    public MySemaphore() {
        this.semaphoreTable = new HashMap<>();
    }

    @Override
    public void put(int key, Pair<Integer, List<Integer>> value) {
        lock.lock();
        try {
            semaphoreTable.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Pair<Integer, List<Integer>> get(int key) {
        lock.lock();
        try {
            return semaphoreTable.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(int key) {
        lock.lock();
        try {
            return semaphoreTable.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getFreeAddress() {
        lock.lock();
        try {
            return freeLocation++;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setFreeAddress(int freeAddress) {
        lock.lock();
        try {
            this.freeLocation = freeAddress;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<Integer, Pair<Integer, List<Integer>>> getContent() {
        lock.lock();
        try {
            return semaphoreTable;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setContent(Map<Integer, Pair<Integer, List<Integer>>> newContent) {
        lock.lock();
        try {
            this.semaphoreTable = newContent;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return semaphoreTable.toString();
        } finally {
            lock.unlock();
        }
    }
}
