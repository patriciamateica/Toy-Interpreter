package model.statement;

import exceptions.MyException;
import model.adt.barrier.IBarrier;
import model.adt.barrier.MyBarrierTable;
import model.state.ProgramState;
import model.adt.stack.IStack;
import model.adt.stack.ExecutionStack;
import model.adt.map.IMap;
import model.adt.list.IList;
import model.adt.dictfile.FileTable;
import model.adt.heap.IHeap;
import model.value.Value;
import model.type.Type;

public class ForkStatement implements Statement {
    private final Statement statement;

    public ForkStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        IList<Value> out = state.getOut();
        FileTable fileTable = state.getFileTable();
        IHeap heap = state.getHeap();
        IBarrier barrierTable = state.getBarrierTable();

        IMap<String, Value> clonedSymTable = symTable.deepCopy();
        IStack childStack = new ExecutionStack();

        return new ProgramState(childStack, clonedSymTable, out, fileTable, statement, heap, barrierTable, 0);
    }

    /*
    * - this method is responsible for creating a new ProgramState that represents a child thread of execution
    * - the current program's state is duplicated and a new execution stack is created for the child
    * - the symbol table is deep-copied to ensure that the child thread has its own copy of variables and doesn't play with the ones the parent has
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        statement.typecheck(typeEnv.deepCopy());
        return typeEnv;
    }

    @Override
    public String toString() {
        return "fork(" + statement.toString() + ")";
    }
}