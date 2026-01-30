
package model.adt.heap;

import model.value.Value;

import java.util.Map;

public interface IHeap<T> {
    int allocate(T value);
    T get(int address);
    void update(int address, T value);
    boolean contains(int address);
    Map<Integer, T> getContent();
    void setContent(Map<Integer, T> content);

}