// java
package model.expression;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.type.Type;
import model.value.Value;

public class ValueExpression implements Expression<Value> {
    private final Value value;

    public ValueExpression(Value value) {
        this.value = value;
    }

    @Override
    public Value evaluate(IMap symTable, IHeap heap) {
        return value;
    }

    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
        return value.getType();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}