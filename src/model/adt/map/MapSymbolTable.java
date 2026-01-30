package model.adt.map;

import model.value.Value;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class MapSymbolTable<K, V> implements IMap<K, V> {
    private final Map<K, V> map;

    public MapSymbolTable() {
        this.map = new HashMap<>();
    }

    @Override
    public boolean isDefined(K key) {
        return map.containsKey(key);
    }

    @Override
    public void update(K key, V value) {
        if (!map.containsKey(key)) {
            throw new RuntimeException("MyMap: key not defined: " + key);
        }
        map.put(key, value);
    }

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public V getValue(K key) {
        if (!map.containsKey(key)) {
            throw new RuntimeException("MyMap: key not defined: " + key);
        }
        return map.get(key);
    }

    @Override
    public Map<K, V> getContent() {
        return new HashMap<>(map);
    }

    @Override
    public Collection<V> values() {
        return new ArrayList<>(map.values());
    }

    @Override
    public IMap<K, V> deepCopy() {
        MapSymbolTable<K, V> copy = new MapSymbolTable<>();
        for (Map.Entry<K, V> e : map.entrySet()) {
            V value = e.getValue();
            V copiedValue = value;
            if (value instanceof Value) {
                @SuppressWarnings("unchecked")
                V tmp = (V) ((Value) value).deepCopy();
                copiedValue = tmp;
            }
            copy.put(e.getKey(), copiedValue);
        }
        return copy;
    }

    @Override
    public void remove(K key) {
        map.remove(key);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}