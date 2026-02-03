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

//this implements the await(var) command. it acts as the "pause button" for threads. when a thread executes this, it checks a specific barrier to see if everyone has arrived
public class AwaitStatement implements Statement {
    private final String var;

    public AwaitStatement(String var) {
        this.var = var;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        if (!state.getSymTable().isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in SymTable");
        }

        Value varValue = (Value) state.getSymTable().getValue(var);
        if (!(varValue instanceof IntegerValue)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        int foundIndex = ((IntegerValue) varValue).value();

        IBarrier barrierTable = state.getBarrierTable();
        if (!barrierTable.contains(foundIndex)) {
            throw new MyException("Index " + foundIndex + " is not in the BarrierTable");
        }

        Pair<java.lang.Integer, List<java.lang.Integer>> barrierEntry = barrierTable.get(foundIndex);
        int n1 = barrierEntry.getFirst();
        List<java.lang.Integer> list1 = barrierEntry.getSecond();
        int nl = list1.size();

        if (n1 > nl) {
            int currentId = state.getId();
            if (list1.contains(currentId)) {
                state.getExeStack().push(this);
            } else {
                list1.add(currentId);
                barrierTable.update(foundIndex, new Pair<>(n1, list1));
                state.getExeStack().push(this);
            }
        }
        // first case: barrier is not full yet so we must wait
            // if i am already on the list, just wait.
            // push 'this' back to stack to re-execute 'await' next step.
            // if I am NOT on the list, add me!
            // save the updated list back to the table
            // wait (push 'this' back to stack)

        // second case: n1 <= nl
        // the barrier is full/open. we do NOT push 'this' back to the stack.
        // the execution simply proceeds to the next statement.
        return null;
    }
    /*
     * handles the synchronization logic
     * checks if the barrier limit has been reached
     * if NOT reached -> adds current thread to list & pauses (pushes await back)
     * if reached -> lets thread continue (does not push await back)
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
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
