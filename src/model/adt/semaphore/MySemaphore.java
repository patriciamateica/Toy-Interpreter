package model.adt.semaphore;

import javafx.util.Pair;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphore implements ISemaphore {
    private Map<Integer, Pair<Integer, List<Integer>>> semaphoreTable; // the storage map: Address -> (Permit Count, List of Waiting Thread IDs)
    private int freeLocation = 1;// tracks the next available unique address for a new semaphore
    private final Lock lock = new ReentrantLock();// lock used to ensure thread safety (mutual exclusion) for all operations

    public MySemaphore() {
        this.semaphoreTable = new HashMap<>();
    }

    /*
     * updates or Inserts a semaphore entry at the specific key.
     * used during 'createSemaphore' (initial setup) or 'acquire'/'release'
     * (updating the list of waiting threads or permit count).
     */
    @Override
    public void put(int key, Pair<Integer, List<Integer>> value) {
        lock.lock();
        try {
            semaphoreTable.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    /*
     * retrieves the semaphore data (Permit Count + Waiting List) for a given address.
     * used by 'acquire' to check if a permit is available.
     */
    @Override
    public Pair<Integer, List<Integer>> get(int key) {
        lock.lock();
        try {
            return semaphoreTable.get(key);
        } finally {
            lock.unlock();
        }
    }

    /*
     * checks if a semaphore exists at the given address.
     * good for validation before trying to access a semaphore.
     */
    @Override
    public boolean containsKey(int key) {
        lock.lock();
        try {
            return semaphoreTable.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    /*
     * returns a unique address for a new semaphore and increments the counter.
     * called exactly once per 'createSemaphore' statement.
     */
    @Override
    public int getFreeAddress() {
        lock.lock();
        try {
            return freeLocation++;
        } finally {
            lock.unlock();
        }
    }

    /*
     * returns the raw map.
     * typically used by the Garbage Collector (to see which semaphores are active)
     * or by the GUI to display the table contents.
     */
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
    public String toString() {
        lock.lock();
        try {
            return semaphoreTable.toString();
        } finally {
            lock.unlock();
        }
    }
}
