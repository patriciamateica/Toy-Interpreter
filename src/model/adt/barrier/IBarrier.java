package model.adt.barrier;

import java.util.List;
import java.util.Map;
import model.adt.barrier.Pair;

public interface IBarrier {
    int allocate(int value, List<Integer> list);
    Pair<Integer, List<Integer>> get(int address);
    void update(int address, Pair<Integer, List<Integer>> value);
    boolean contains(int address);
    Map<Integer, Pair<Integer, List<Integer>>> getContent();
    void setContent(Map<Integer, Pair<Integer, List<Integer>>> content);
}
