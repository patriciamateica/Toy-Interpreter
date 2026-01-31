package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.lock.ILockTable;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

public class LockStatement implements Statement {
    private final String var;

    public LockStatement(String var) {
        this.var = var;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        ILockTable lockTable = state.getLockTable();

        // Check if variable exists in symbol table
        if (!symTable.isDefined(var)) {
            throw new MyException("LockStatement: Variable " + var + " is not defined");
        }

        // Check if variable has type int
        Value varValue = symTable.getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("LockStatement: Variable " + var + " must be of type int");
        }

        // Get the lock index from the variable
        int foundIndex = ((IntegerValue) varValue).value();

        // Check if foundIndex exists in LockTable
        if (!lockTable.contains(foundIndex)) {
            throw new MyException("LockStatement: Index " + foundIndex + " is not in LockTable");
        }

        // Atomic operation: check and update lock
        synchronized (lockTable) {
            int lockValue = lockTable.get(foundIndex);

            if (lockValue == -1) {
                // Lock is free, acquire it
                lockTable.update(foundIndex, state.getId());
            } else {
                // Lock is held by another program state, push statement back
                state.getExeStack().push(this);
            }
        }

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        // Check if variable exists in type environment
        if (!typeEnv.isDefined(var)) {
            throw new MyException("LockStatement: Variable " + var + " is not defined in type environment");
        }

        // Check if variable has type int
        Type varType = typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("LockStatement: Variable " + var + " must be of type int, but found " + varType);
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "lock(" + var + ")";
    }
}
