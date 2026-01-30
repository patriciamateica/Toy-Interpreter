package model.adt.map;

import model.value.Value;

import java.util.Collection;
import java.util.Map;

public interface IMap<K, V> {
    boolean isDefined(K key);
    void update(K key, V value);
    void put(K key, V value);
    V getValue(K key);
    Map<K, V> getContent();
    Collection<V> values();
    IMap<K, V> deepCopy();
    void remove(K key);
}