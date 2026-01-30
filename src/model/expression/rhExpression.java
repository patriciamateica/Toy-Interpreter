package model.expression;

import exceptions.HeapNullException;
import exceptions.InvalidTypeException;
import exceptions.MyException;
import exceptions.NotAHeapAddressException;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.type.RefType;
import model.type.Type;
import model.value.Value;
import model.value.RefValue;

public class rhExpression implements Expression<Value> {
    private final Expression<Value> expr;

    public rhExpression(Expression<Value> expr) {
        this.expr = expr;
    }

    @Override
    public Value evaluate(IMap<String, Value> symTable, IHeap<Value> heap) {
        Value v = expr.evaluate(symTable, heap);
        if (!(v instanceof RefValue)) {
            throw new InvalidTypeException("rH: expression does not evaluate to a RefValue");
        }
        int address = ((RefValue) v).getAddress();
        if (heap == null) {
            throw new HeapNullException("rH: heap is null");
        }
        if (!heap.contains(address)) {
            throw new NotAHeapAddressException("rH: address " + address + " not present in heap");
        }
        return heap.get(address);
    }

    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type typ = expr.typecheck(typeEnv);
        if (typ instanceof RefType) {
            RefType reft = (RefType) typ;
            return reft.getInner();
        } else {
            throw new InvalidTypeException("rH: expression does not evaluate to a RefValue");
        }
    }

    @Override
    public String toString() {
        return "rH(" + expr + ")";
    }
}