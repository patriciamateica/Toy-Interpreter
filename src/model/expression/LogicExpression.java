package model.expression;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.value.Value;
import model.value.BooleanValue;
import model.type.Boolean;
import model.type.Type;

public class LogicExpression implements Expression<Value> {
    private final Expression<Value> e1;
    private final Expression<Value> e2;
    private final int op;

    public LogicExpression(int op, Expression<Value> e1, Expression<Value> e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public Value evaluate(IMap<String, Value> symTable, IHeap<Value> heap) {
        Value v1 = e1.evaluate(symTable, heap);
        if (!v1.getType().equals(new Boolean())) {
            throw new MyException("Operand1 is not a boolean");
        }

        Value v2 = e2.evaluate(symTable, heap);
        if (!v2.getType().equals(new Boolean())) {
            throw new MyException("Operand2 is not a boolean");
        }

        boolean b1 = ((BooleanValue) v1).value();
        boolean b2 = ((BooleanValue) v2).value();

        switch (op) {
            case 1: return new BooleanValue(b1 && b2);
            case 2: return new BooleanValue(b1 || b2);
            default: throw new MyException("Invalid logical operator: " + op);
        }
    }

    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type t1 = e1.typecheck(typeEnv);
        Type t2 = e2.typecheck(typeEnv);

        if (t1.equals(new Boolean())) {
            if (t2.equals(new Boolean())) {
                return new Boolean();
            } else {
                throw new MyException("right operand is not a boolean");
            }
        } else {
            throw new MyException("left operand is not a boolean");
        }
    }

    @Override
    public String toString() {
        String opStr = (op == 1) ? " && " : (op == 2) ? " || " : " ? ";
        return "(" + e1.toString() + opStr + e2.toString() + ")";
    }
}