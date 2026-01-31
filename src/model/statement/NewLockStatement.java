package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.lock.ILockTable;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

public class NewLockStatement implements Statement {
    private final String var;

    public NewLockStatement(String var) {
        this.var = var;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        ILockTable lockTable = state.getLockTable();

        // Check if variable exists in symbol table
        if (!symTable.isDefined(var)) {
            throw new MyException("NewLockStatement: Variable " + var + " is not defined");
        }

        // Check if variable has type int
        Value varValue = symTable.getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("NewLockStatement: Variable " + var + " must be of type int");
        }

        // Allocate new lock location (synchronized operation)
        int newFreeLocation = lockTable.allocate();

        // Update symbol table with the new lock location
        symTable.update(var, new IntegerValue(newFreeLocation));

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        // Check if variable exists in type environment
        if (!typeEnv.isDefined(var)) {
            throw new MyException("NewLockStatement: Variable " + var + " is not defined in type environment");
        }

        // Check if variable has type int
        Type varType = typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("NewLockStatement: Variable " + var + " must be of type int, but found " + varType);
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "newLock(" + var + ")";
    }
}
