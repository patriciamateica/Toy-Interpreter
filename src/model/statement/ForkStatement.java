package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.adt.stack.IStack;
import model.adt.stack.ExecutionStack;
import model.adt.map.IMap;
import model.adt.list.IList;
import model.adt.dictfile.FileTable;
import model.adt.heap.IHeap;
import model.adt.latch.ILatch;
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
        IHeap<Value> heap = state.getHeap();
        ILatch latchTable = state.getLatchTable();

        IMap<String, Value> clonedSymTable = symTable.deepCopy();

        IStack childStack = new ExecutionStack();

        return new ProgramState(childStack, clonedSymTable, out, fileTable, statement, heap, latchTable);
    }

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
