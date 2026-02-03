package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.adt.stack.IStack;
import model.adt.stack.ExecutionStack;
import model.adt.map.IMap;
import model.adt.list.IList;
import model.adt.dictfile.FileTable;
import model.adt.heap.IHeap;
import model.adt.procedures.IProcTable;
import model.value.Value;
import model.type.Type;

import java.util.Stack;

public class ForkStatement implements Statement {
    private final Statement statement;

    public ForkStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Stack<IMap<String, Value>> symTableStack = state.getSymTableStack();
        IList<Value> out = state.getOut();
        FileTable fileTable = state.getFileTable();
        IHeap heap = state.getHeap();
        IProcTable procTable = state.getProcTable();

        Stack<IMap<String, Value>> clonedSymTableStack = new Stack<>();
        for (IMap<String, Value> symTable : symTableStack) {
            clonedSymTableStack.push(symTable.deepCopy());
        }

        IStack childStack = new ExecutionStack();

        return new ProgramState(childStack, clonedSymTableStack, out, fileTable, statement, heap, procTable, 0);
    } // added procedure entry table

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
