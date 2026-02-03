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

public class AcquireStatement implements Statement {
    private final String var;

    public AcquireStatement(String var) {
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

        int NL = List1.size();
        int currentPrgStateId = state.getId();

        if (N1 > NL) {
            if (!List1.contains(currentPrgStateId)) {
                List<java.lang.Integer> newList = new ArrayList<>(List1);
                newList.add(currentPrgStateId);
                semaphoreTable.put(foundIndex, new Pair<>(N1, newList));
            }
        } else {
            state.getExeStack().push(this);
        }
        // first case: space is available (Max > Current)
            // we are not already in the list, so we take a spot.
            // ee copy the list to ensure immutability/safety before updating
            // update the table with the new list containing our ID
            // we successfully acquired, not pushing anything back to stack.
            // execution proceeds to the next statement.

        // second case: semaphore is FULL (Max <= Current)
            // we cannot enter. we must wait.
            // push 'this' (AcquireStatement) back onto the stack.
            // this forces the interpreter to execute 'acquire' again in the next step (busy wait).

        return null;
    }
    /*
     * handles the logic for acquiring a permit.
     * if permits are available (N > Size), take one (add ID to list).
     * if NO permits available, wait (push 'this' back to stack).
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
    //ensures 'var' is defined and of type int.

    @Override
    public String toString() {
        return "acquire(" + var + ")";
    }
}
