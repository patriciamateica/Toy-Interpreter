package model.statement;

import exceptions.MyException;
import javafx.util.Pair;
import model.adt.map.IMap;
import model.adt.semaphore.ISemaphore;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

import java.util.ArrayList;
import java.util.List;

public class ReleaseStatement implements Statement {
    private final String var;

    public ReleaseStatement(String var) {
        this.var = var;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        ISemaphore semaphoreTable = state.getSemaphoreTable();

        if (!symTable.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the symbol table");
        }

        Value varValue = symTable.getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        int foundIndex = ((IntegerValue) varValue).value();

        if (!semaphoreTable.containsKey(foundIndex)) {
            throw new MyException("Index " + foundIndex + " is not in the semaphore table");
        }

        Pair<java.lang.Integer, List<java.lang.Integer>> entry = semaphoreTable.get(foundIndex);
        int N1 = entry.getKey();
        List<java.lang.Integer> List1 = entry.getValue();

        int currentPrgStateId = state.getId();

        if (List1.contains(currentPrgStateId)) {
            List<java.lang.Integer> newList = new ArrayList<>(List1);
            newList.remove((java.lang.Integer) currentPrgStateId);

            semaphoreTable.put(foundIndex, new Pair<>(N1, newList));
        }

        return null;
    }
    /*
     * handles the logic for releasing a permit.
     * finds the semaphore.
     * removes the current thread ID from the list of "active" threads.
     * this effectively "opens a slot" for other waiting threads.
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        if (!typeEnv.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the type environment");
        }
        Type varType = typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "release(" + var + ")";
    }
}
