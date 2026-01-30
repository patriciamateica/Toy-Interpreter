package model.expression;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.type.Type;
import model.type.Boolean;
import model.value.Value;
import model.value.BooleanValue;

/**
 * Unary logical NOT: !exp
 */
public class NotExpression implements Expression<Value> {
    private final Expression<Value> exp;

    public NotExpression(Expression<Value> exp) {
        this.exp = exp;
    }

    @Override
    public Value evaluate(IMap<String, Value> symTable, IHeap<Value> heap) {
        Value v = exp.evaluate(symTable, heap);
        if (!(v instanceof BooleanValue))
            throw new RuntimeException("NotExpression: operand is not a boolean");
        boolean val = ((BooleanValue) v).value();
        return new BooleanValue(!val);
    }

    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type t = exp.typecheck(typeEnv);
        if (!t.equals(new Boolean()))
            throw new MyException("NotExpression: operand is not a boolean");
        return new Boolean();
    }

    @Override
    public String toString() {
        return "(!" + exp + ")";
    }
}
