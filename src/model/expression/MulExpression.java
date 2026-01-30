package model.expression;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.type.Type;
import model.type.Integer;
import model.value.Value;
import model.value.IntegerValue;

/**
 * MUL(exp1, exp2) => (exp1 * exp2) - (exp1 + exp2)
 */
public class MulExpression implements Expression<Value> {
    private final Expression<Value> exp1;
    private final Expression<Value> exp2;

    public MulExpression(Expression<Value> exp1, Expression<Value> exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    public Value evaluate(IMap<String, Value> symTable, IHeap<Value> heap) {
        Value v1 = exp1.evaluate(symTable, heap);
        Value v2 = exp2.evaluate(symTable, heap);

        if (!(v1 instanceof IntegerValue) || !(v2 instanceof IntegerValue)) {
            throw new RuntimeException("MulExpression: operands must be integers");
        }

        int a = ((IntegerValue) v1).value();
        int b = ((IntegerValue) v2).value();
        int result = (a * b) - (a + b);
        return new IntegerValue(result);
    }

    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type t1 = exp1.typecheck(typeEnv);
        Type t2 = exp2.typecheck(typeEnv);

        if (!t1.equals(new Integer()) || !t2.equals(new Integer())) {
            throw new MyException("MulExpression: both operands must be of type int");
        }

        return new Integer();
    }

    @Override
    public String toString() {
        return String.format("MUL(%s,%s)", exp1, exp2);
    }
}
