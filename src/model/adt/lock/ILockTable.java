package model.adt.lock;

import java.util.Map;

public interface ILockTable {
    int allocate();
    int get(int location);
    void update(int location, int value);
    boolean contains(int location);
    Map<Integer, Integer> getContent();
    void setContent(Map<Integer, Integer> content);
}
