package model.statement;

import exceptions.MyException;
import model.adt.latch.ILatch;
import model.adt.map.IMap;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

public class CountDownStatement implements Statement {
    private final String var;

    public CountDownStatement(String var) {
        this.var = var;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        ILatch latchTable = state.getLatchTable();

        if (!symTable.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the symbol table.");
        }

        Value varValue = symTable.getValue(var);
        if (!varValue.getType().equals(new Integer())) {
            throw new MyException("Variable " + var + " is not of type int.");
        }

        int foundIndex = ((IntegerValue) varValue).value();

        synchronized (latchTable) {
            if (!latchTable.contains(foundIndex)) {
                throw new MyException("Index " + foundIndex + " not found in the LatchTable.");
            }

            int latchValue = latchTable.get(foundIndex);
            if (latchValue > 0) {
                latchTable.update(foundIndex, latchValue - 1);
            }
            state.getOut().add(new IntegerValue(state.getId()));
        }

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        if (!typeEnv.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined.");
        }
        Type varType = typeEnv.getValue(var);
        if (!varType.equals(new Integer())) {
            throw new MyException("Variable " + var + " is not of type int.");
        }
        return typeEnv;
    }

    @Override
    public String toString() {
        return "countDown(" + var + ")";
    }
}
