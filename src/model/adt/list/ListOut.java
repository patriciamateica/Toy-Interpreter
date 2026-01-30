package model.adt.list;


import exceptions.MyException;

import java.util.ArrayList;
import java.util.List;

public class ListOut<T> implements IList<T> {
    private final List<T> outputList;

    public ListOut() {

        outputList = new ArrayList<>();

    }

    @Override
    public void add(T value) {
        outputList.add(value);
    }

    @Override
    public List<T> getAll(){
        return outputList;
    }

    @Override
    public String toString() {
        return outputList.toString();
    }
}
