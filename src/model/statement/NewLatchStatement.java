package model.statement;

import exceptions.MyException;
import model.adt.heap.IHeap;
import model.adt.latch.ILatch;
import model.adt.map.IMap;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

public class NewLatchStatement implements Statement {
    private final String var;
    private final Expression<Value> exp;

    public NewLatchStatement(String var, Expression<Value> exp) {
        this.var = var;
        this.exp = exp;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        IHeap<Value> heap = state.getHeap();
        ILatch latchTable = state.getLatchTable();

        Value expValue = exp.evaluate(symTable, heap);
        if (!expValue.getType().equals(new Integer())) {
            throw new MyException("Expression evaluation is not of type int");
        }

        if (!symTable.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the symbol table");
        }

        Value varValue = symTable.getValue(var);
        if (!varValue.getType().equals(new Integer())) {
            throw new MyException("Variable " + var + " is not of type int");
        }

        int num1 = ((IntegerValue) expValue).value();
        int newFreeLocation = latchTable.allocate(num1);
        symTable.update(var, new IntegerValue(newFreeLocation));

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type varType = typeEnv.getValue(var);
        Type expType = exp.typecheck(typeEnv);

        if (!varType.equals(new Integer())) {
            throw new MyException("Variable " + var + " is not of type int");
        }
        if (!expType.equals(new Integer())) {
            throw new MyException("Expression is not of type int");
        }
        return typeEnv;
    }

    @Override
    public String toString() {
        return "newLatch(" + var + ", " + exp.toString() + ")";
    }
}
