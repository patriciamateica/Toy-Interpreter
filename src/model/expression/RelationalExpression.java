package model.expression;

import exceptions.MyException;
import exceptions.NotARelationalExpression;
import model.adt.heap.IHeap;
import model.adt.map.IMap;
import model.value.BooleanValue;
import model.value.IntegerValue;
import model.value.Value;
import model.type.Type;

public class RelationalExpression implements Expression<Value> {
    private final Expression<Value> left;
    private final Expression<Value> right;
    private final String op;

    public RelationalExpression(String op, Expression<Value> left, Expression<Value> right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public Value evaluate(IMap<String, Value> symTable, IHeap<Value> heap) {
        Value v1 = left.evaluate(symTable, heap);
        if (!(v1 instanceof IntegerValue)) {
            throw new MyException("Left operand is not an integer in relational expression");
        }
        Value v2 = right.evaluate(symTable, heap);
        if (!(v2 instanceof IntegerValue)) {
            throw new MyException("Right operand is not an integer in relational expression");
        }

        int n1 = ((IntegerValue) v1).value();
        int n2 = ((IntegerValue) v2).value();
        boolean result;

        switch (op) {
            case "<":  result = n1 < n2; break;
            case "<=": result = n1 <= n2; break;
            case "==": result = n1 == n2; break;
            case "!=": result = n1 != n2; break;
            case ">":  result = n1 > n2; break;
            case ">=": result = n1 >= n2; break;
            default:
                throw new NotARelationalExpression("Unknown relational operator: " + op);
        }

        return new BooleanValue(result);
    }

    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type typ1 = left.typecheck(typeEnv);
        Type typ2 = right.typecheck(typeEnv);

        if (typ1.equals(new model.type.Integer())) {
            if (typ2.equals(new model.type.Integer())) {
                return new model.type.Boolean();
            } else {
                throw new MyException("right operand is not an integer");
            }
        } else {
            throw new MyException("left operand is not an integer");
        }
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + op + " " + right.toString() + ")";
    }
}