package model.statement;

import exceptions.MyException;
import model.adt.barrier.IBarrier;
import model.adt.barrier.Pair;
import model.adt.map.IMap;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

import java.util.List;

public class AwaitStatement implements Statement {
    private final String var;

    public AwaitStatement(String var) {
        this.var = var;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        // Get foundIndex from symbol table
        if (!state.getSymTable().isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in SymTable");
        }

        Value varValue = (Value) state.getSymTable().getValue(var);
        if (!(varValue instanceof IntegerValue)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        int foundIndex = ((IntegerValue) varValue).value();

        // Check if foundIndex exists in barrier table
        IBarrier barrierTable = state.getBarrierTable();
        if (!barrierTable.contains(foundIndex)) {
            throw new MyException("Index " + foundIndex + " is not in the BarrierTable");
        }

        // Retrieve the barrier entry
        Pair<java.lang.Integer, List<java.lang.Integer>> barrierEntry = barrierTable.get(foundIndex);
        int n1 = barrierEntry.getFirst();
        List<java.lang.Integer> list1 = barrierEntry.getSecond();
        int nl = list1.size();

        // Check if N1 > NL
        if (n1 > nl) {
            int currentId = state.getId();
            if (list1.contains(currentId)) {
                // Push back await(var) on the ExeStack
                state.getExeStack().push(this);
            } else {
                // Add current thread ID to the list
                list1.add(currentId);
                // Update the barrier table
                barrierTable.update(foundIndex, new Pair<>(n1, list1));
                // Push back await(var) on the ExeStack
                state.getExeStack().push(this);
            }
        }
        // If N1 <= NL, do nothing (barrier released)

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        // Check if variable is defined and is of type int
        if (!typeEnv.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined");
        }

        Type varType = (Type) typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "await(" + var + ")";
    }
}
