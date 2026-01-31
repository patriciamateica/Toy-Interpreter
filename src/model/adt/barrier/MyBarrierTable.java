package model.adt.barrier;
import model.adt.barrier.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBarrierTable implements IBarrier {
    private Map<Integer, Pair<Integer, List<Integer>>> barrierTable;
    private int freeLocation = 1;
    private final Lock lock = new ReentrantLock();

    public MyBarrierTable() {
        this.barrierTable = new HashMap<>();
    }

    @Override
    public synchronized int allocate(int value, List<Integer> list) {
        lock.lock();
        try {
            int address = freeLocation++;
            barrierTable.put(address, new Pair<>(value, list));
            return address;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Pair<Integer, List<Integer>> get(int address) {
        lock.lock();
        try {
            return barrierTable.get(address);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(int address, Pair<Integer, List<Integer>> value) {
        lock.lock();
        try {
            barrierTable.put(address, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(int address) {
        lock.lock();
        try {
            return barrierTable.containsKey(address);
        } finally {
            lock.unlock();
        }
    }

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
