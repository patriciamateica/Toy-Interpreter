package model.adt.barrier;
import model.adt.barrier.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBarrierTable implements IBarrier {
    private Map<Integer, Pair<Integer, List<Integer>>> barrierTable; // maps a unique address -> (Barrier Limit N, List of Arrived Threads)
    private int freeLocation = 1; // counter to ensure every new barrier gets a unique address
    private final Lock lock = new ReentrantLock(); // lock to ensure thread safety during concurrent access



    public MyBarrierTable() {
        this.barrierTable = new HashMap<>();
    }

    /*
     * creates a new barrier entry in the table
     * @param value the barrier capacity (N) - how many threads must wait
     * @param list the initial list of threads (usually empty)
     * @return the unique address (ID) allocated for this barrier
     */
    @Override
    public synchronized int allocate(int value, List<Integer> list) {
        lock.lock();
        try {
            int address = freeLocation++;
            barrierTable.put(address, new Pair<>(value, list));
            return address;
        } finally {
            lock.unlock(); //used to prevent deadlocks
        }
    }


    /*
     * retrieves the barrier data associated with the given address
     * @param address the ID of the barrier
     * @return a pair containing the limit N and the list of waiting threads
     */
    @Override
    public Pair<Integer, List<Integer>> get(int address) {
        lock.lock();
        try {
            return barrierTable.get(address);
        } finally {
            lock.unlock();
        }
    }

    /*
     * updates the entry at a specific address
     * used when a thread hits 'await' and needs to add itself to the list
     * @param address the ID of the barrier to update.
     * @param value the new pair (same limit N, updated list of threads).
     */
    @Override
    public void update(int address, Pair<Integer, List<Integer>> value) {
        lock.lock();
        try {
            barrierTable.put(address, value);
        } finally {
            lock.unlock();
        }
    }

    //checks if a barrier entry exists for a given address
    @Override
    public boolean contains(int address) {
        lock.lock();
        try {
            return barrierTable.containsKey(address);
        } finally {
            lock.unlock();
        }
    }

    //returns a copy of the entire barrier table
    @Override
    public Map<Integer, Pair<Integer, List<Integer>>> getContent() {
        lock.lock();
        try {
            return new HashMap<>(barrierTable);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setContent(Map<Integer, Pair<Integer, List<Integer>>> content) {
        lock.lock();
        try {
            this.barrierTable = new HashMap<>(content);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            return barrierTable.toString();
        } finally {
            lock.unlock();
        }
    }
}
