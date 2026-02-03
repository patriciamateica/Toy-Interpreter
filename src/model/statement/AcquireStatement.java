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

        // Check if var exists in SymTable and has type int
        if (!symTable.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the symbol table");
        }

        Value varValue = symTable.getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        int foundIndex = ((IntegerValue) varValue).value();

        // Check if foundIndex is in SemaphoreTable (atomic operation)
        if (!semaphoreTable.containsKey(foundIndex)) {
            throw new MyException("Index " + foundIndex + " is not in the semaphore table");
        }

        // Retrieve the entry (atomic operation)
        Pair<java.lang.Integer, List<java.lang.Integer>> entry = semaphoreTable.get(foundIndex);
        int N1 = entry.getKey();
        List<java.lang.Integer> List1 = entry.getValue();

        int NL = List1.size();
        int currentPrgStateId = state.getId();

        if (N1 > NL) {
            // Check if current PrgState id is already in List1
            if (!List1.contains(currentPrgStateId)) {
                // Add current PrgState id to List1 (atomic operation)
                List<java.lang.Integer> newList = new ArrayList<>(List1);
                newList.add(currentPrgStateId);
                semaphoreTable.put(foundIndex, new Pair<>(N1, newList));
            }
        } else {
            // Push acquire(var) back on the ExeStack
            state.getExeStack().push(this);
        }

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        // Check if var exists in type environment
        if (!typeEnv.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the type environment");
        }

        // Check if var has type int
        Type varType = typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "acquire(" + var + ")";
    }
}
