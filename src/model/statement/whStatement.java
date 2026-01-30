package model.statement;

import exceptions.*;
import model.state.ProgramState;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.expression.Expression;
import model.value.Value;
import model.value.RefValue;
import model.type.Type;
import model.type.RefType;

public class whStatement implements Statement {
    private final String varName;
    private final Expression expr;

    public whStatement(String varName, Expression expr) {
        this.varName = varName;
        this.expr = expr;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        IHeap<Value> heap = state.getHeap();

        if (!symTable.isDefined(varName)) {
            throw new UndefinedVariableException("wH: variable not defined: " + varName);
        }

        Value v = symTable.getValue(varName);
        if (!(v instanceof RefValue)) {
            throw new InvalidTypeException("wH: variable " + varName + " is not a RefValue!");
        }
        RefValue rv = (RefValue) v;

        int addr = rv.getAddress();
        if (!heap.contains(addr)) {
            throw new NotAHeapAddressException("wH: memory at address " + addr + " is not allocated!");
        }

        Object evalResult = expr.evaluate(symTable, heap);
        if (!(evalResult instanceof Value)) {
            throw new MyException("wH: expression did not evaluate to a Value");
        }
        Value newV = (Value) evalResult;

        if (!newV.getType().equals(rv.getLocationType())) {
            throw new InvalidTypeException("wH: type mismatch - expected " + rv.getLocationType() + ", found " + newV.getType());
        }

        heap.update(addr, newV);
        return state;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type varType = typeEnv.getValue(varName);
        if (varType == null) {
            throw new MyException("wH: variable " + varName + " is not declared");
        }
        if (!(varType instanceof RefType)) {
            throw new MyException("wH: variable " + varName + " is not a RefType");
        }
        RefType refType = (RefType) varType;
        Type exprType = expr.typecheck(typeEnv);
        if (!exprType.equals(refType.getInner())) {
            throw new MyException("wH: type mismatch - variable " + varName + " refers to " + refType.getInner() + ", expression has type " + exprType);
        }
        return typeEnv;
    }
    /*
    * - evaluate the expression in the context of the symbol table.
     * - ensure the variable is defined and is of RefType.
     * - ensure the evaluated expression type matches the location type of the RefValue.
     * - update the heap at the address stored in the RefValue with the new value.
     */

    @Override
    public String toString() {
        return "wH(" + varName + ", " + expr + ")";
    }
}