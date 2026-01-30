package model.expression;

import exceptions.DivisionByZeroException;
import exceptions.MyException;
import model.adt.heap.IHeap;
import model.adt.map.IMap;
import model.value.Value;
import model.value.IntegerValue;
import model.type.Integer;
import model.type.Type;

public class ArithmeticExpression implements Expression<Value> {
    private final Expression<Value> e1;
    private final Expression<Value> e2;
    private final int operator;

    public ArithmeticExpression(int operator, Expression<Value> e1, Expression<Value> e2) {
        this.operator = operator;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public Value evaluate(IMap<String, Value> symTable, IHeap<Value> heap) {
        Value v1 = e1.evaluate(symTable, heap);
        if (!v1.getType().equals(new Integer()))
            throw new MyException("first operand is not an integer");

        Value v2 = e2.evaluate(symTable, heap);
        if (!v2.getType().equals(new Integer()))
            throw new MyException("second operand is not an integer");

        int n1 = ((IntegerValue) v1).value();
        int n2 = ((IntegerValue) v2).value();

        switch (operator) {
            case 1: return new IntegerValue(n1 + n2);
            case 2: return new IntegerValue(n1 - n2);
            case 3: return new IntegerValue(n1 * n2);
            case 4:
                if (n2 == 0) throw new DivisionByZeroException("division by zero");
                return new IntegerValue(n1 / n2);
            default:
                throw new MyException("invalid arithmetic operator: " + operator);
        }
    }

    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type typ1 = e1.typecheck(typeEnv);
        Type typ2 = e2.typecheck(typeEnv);

        if (typ1.equals(new Integer())) {
            if (typ2.equals(new Integer())) {
                return new Integer();
            } else {
                throw new MyException("second operand is not an integer");
            }
        } else {
            throw new MyException("first operand is not an integer");
        }
    }

    @Override
    public String toString() {
        String op;
        switch (operator) {
            case 1: op = " + "; break;
            case 2: op = " - "; break;
            case 3: op = " * "; break;
            case 4: op = " / "; break;
            default: op = " ? "; break;
        }
        return "(" + e1.toString() + op + e2.toString() + ")";
    }
}