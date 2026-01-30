package model.expression;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.type.Type;
import model.value.Value;

public interface Expression<V> {
    V evaluate(IMap<String, V> symTable, IHeap<V> heap);
    Type typecheck(IMap<String, Type> typeEnv) throws MyException;
}