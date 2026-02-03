package model.adt.semaphore;

import java.util.List;
import javafx.util.Pair;
import java.util.Map;

public interface ISemaphore {
    void put(int key, Pair<Integer, List<Integer>> value);
    Pair<Integer, List<Integer>> get(int key);
    boolean containsKey(int key);
    int getFreeAddress();
    void setFreeAddress(int freeAddress);
    Map<Integer, Pair<Integer, List<Integer>>> getContent();
    void setContent(Map<Integer, Pair<Integer, List<Integer>>> newContent);
}
