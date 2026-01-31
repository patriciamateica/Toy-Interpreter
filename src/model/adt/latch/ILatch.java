package model.adt.latch;

import java.util.Map;

public interface ILatch {
    void put(int address, int value);
    int get(int address);
    void update(int address, int value);
    boolean contains(int address);
    int allocate(int value);
    Map<Integer, Integer> getContent();
    void setContent(Map<Integer, Integer> content);
}
