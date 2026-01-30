package model.adt.list;

import java.util.List;

public interface IList<T> {
    void add(T value);
    List<T> getAll();
}
