package model.adt.heap;

import exceptions.UndefinedAddressException;
import model.value.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

public class MyHeap<T extends Value> implements IHeap<T> {
    private Map<Integer, T> heap;
    private int freeAddress;

    public MyHeap() {
        this.heap = new HashMap<>();
        this.freeAddress = 1;
    }

    @Override
    public int allocate(T value) {
        while (heap.containsKey(freeAddress)) {
            freeAddress++;
        }
        int addr = freeAddress;
        heap.put(addr, value);
        freeAddress++;
        return addr;
    }
    /*
    * - allocate starts at the first free value of freeAddress
    * - if freeAddress is already used, we increment it until we find a free one
    * - we put the address and its corresponding value in the heap
    * - we return the allocated address
    */

    @Override
    public T get(int address) {
        if (!heap.containsKey(address)) {
            throw new UndefinedAddressException("Heap: address not defined: " + address);
        }
        return heap.get(address);
    }

    @Override
    public void update(int address, T value) {
        if (!heap.containsKey(address)) {
            throw new UndefinedAddressException("Heap: address not defined: " + address);
        }
        heap.put(address, value);
    }

    @Override
    public boolean contains(int address) {
        return heap.containsKey(address);
    }

    @Override
    public Map<Integer, T> getContent() {
        return new HashMap<>(heap);
    }
    /*
    * returns a copy of the heap
    * this prevents us from modifying the heap directly from outside, it is safer this way
     */

    @Override
    public void setContent(Map<Integer, T> content) {
        this.heap = new HashMap<>(Objects.requireNonNull(content));
        OptionalInt max = heap.keySet().stream().mapToInt(Integer::intValue).max();
        this.freeAddress = max.isPresent() ? max.getAsInt() + 1 : 1;
    }
    /*
    * - sets the heap to the given content
    * - updates freeAddress to be one more than the maximum address in the new content
    * - if the new content is empty, freeAddress is set to 1
    */

    @Override
    public String toString() {
        return heap.toString();
    }
}