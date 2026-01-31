package model.adt.latch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MyLatchTable implements ILatch {
    private Map<Integer, Integer> latchTable;
    private final AtomicInteger nextFreeLocation = new AtomicInteger(1);

    public MyLatchTable() {
        this.latchTable = new HashMap<>();
    }

    @Override
    public synchronized void put(int address, int value) {
        latchTable.put(address, value);
    }

    @Override
    public synchronized int get(int address) {
        return latchTable.get(address);
    }

    @Override
    public synchronized void update(int address, int value) {
        latchTable.put(address, value);
    }

    @Override
    public synchronized boolean contains(int address) {
        return latchTable.containsKey(address);
    }

    @Override
    public int allocate(int value) {
        int newAddress = nextFreeLocation.getAndIncrement();
        put(newAddress, value);
        return newAddress;
    }

    @Override
    public synchronized Map<Integer, Integer> getContent() {
        return latchTable;
    }

    @Override
    public synchronized void setContent(Map<Integer, Integer> content) {
        this.latchTable = content;
    }
}
