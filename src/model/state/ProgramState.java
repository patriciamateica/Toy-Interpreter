package model.state;

import exceptions.MyException;
import model.adt.dictfile.FileTable;
import model.adt.stack.IStack;
import model.adt.map.IMap;
import model.adt.list.IList;
import model.statement.Statement;
import model.value.Value;
import model.adt.heap.IHeap;
import model.adt.procedures.IProcTable;

import java.util.Stack;

public class ProgramState {
    private IStack exeStack;
    private Stack<IMap<String, Value>> symTableStack;
    private IList<Value> out;
    private FileTable fileTable;
    private Statement originalProgram;
    private IHeap heap;
    private IProcTable procTable;
    private final int id;
    private static int lastId = 0;

    public ProgramState(IStack stk, IMap<String, Value> symtbl, IList<Value> ot, FileTable fileTable, Statement prg, IHeap heap, IProcTable procTable, int ignoredId) {
        this.exeStack = stk;
        this.symTableStack = new Stack<>();
        this.symTableStack.push(symtbl);
        this.out = ot;
        this.fileTable = fileTable;
        this.originalProgram = prg;
        if (this.originalProgram != null) {
            this.exeStack.push(this.originalProgram);
        }
        this.heap = heap;
        this.procTable = procTable;
        this.id = nextId();
    }

    public ProgramState(IStack stk, Stack<IMap<String, Value>> symTableStack, IList<Value> ot, FileTable fileTable, Statement prg, IHeap heap, IProcTable procTable, int ignoredId) {
        this.exeStack = stk;
        this.symTableStack = symTableStack;
        this.out = ot;
        this.fileTable = fileTable;
        this.originalProgram = prg;
        if (this.originalProgram != null) {
            this.exeStack.push(this.originalProgram);
        }
        this.heap = heap;
        this.procTable = procTable;
        this.id = nextId();
    }

    public ProgramState(IStack stk, IMap<String, Value> symtbl, IList<Value> ot, FileTable fileTable, IHeap heap, IProcTable procTable, int ignoredId) {
        this(stk, symtbl, ot, fileTable, null, heap, procTable, ignoredId);
    }

    public IStack getExeStack() {
        return exeStack;
    }

    public IMap<String, Value> getSymTable() {
        return symTableStack.isEmpty() ? null : symTableStack.peek();
    }

    public Stack<IMap<String, Value>> getSymTableStack() {
        return symTableStack;
    }

    public void pushSymTable(IMap<String, Value> newSymTable) {
        symTableStack.push(newSymTable);
    }

    public IMap<String, Value> popSymTable() {
        if (symTableStack.size() > 1) {
            return symTableStack.pop();
        }
        throw new RuntimeException("Cannot pop the last symbol table");
    }

    public IList<Value> getOut() {
        return out;
    }

    public IList<Value> out() {
        return out;
    }

    public Statement getOriginalProgram() {
        return originalProgram;
    }

    public void setOriginalProgram(Statement originalProgram) {
        this.originalProgram = originalProgram;
    }

    public void setExeStack(IStack exeStack) {
        this.exeStack = exeStack;
    }

    public void setSymTable(IMap<String, Value> symTable) {
        if (!symTableStack.isEmpty()) {
            symTableStack.pop();
        }
        symTableStack.push(symTable);
    }

    public void setSymTableStack(Stack<IMap<String, Value>> symTableStack) {
        this.symTableStack = symTableStack;
    }

    public void setOut(IList<Value> out) {
        this.out = out;
    }

    public FileTable getFileTable() {
        return fileTable;
    }

    public void setFileTable(FileTable fileTable) {
        this.fileTable = fileTable;
    }

    public IHeap getHeap() {
        return heap;
    }

    public void setHeap(IHeap heap) {
        this.heap = heap;
    }

    public IProcTable getProcTable() {
        return procTable;
    }

    public void setProcTable(IProcTable procTable) {
        this.procTable = procTable;
    }

    public int getId() {
        return id;
    }

    private static synchronized int nextId() {
        return ++lastId;
    }

    public boolean isNotCompleted() {
        return !exeStack.isEmpty();
    }

    public ProgramState oneStep() throws MyException {
        if (exeStack.isEmpty()) {
            throw new MyException("prgstate stack is empty");
        }
        Statement crtStmt = exeStack.pop();
        return crtStmt.execute(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProgramState[id=").append(id).append("]\n");
        sb.append("  exeStack=").append(exeStack).append(",\n");
        sb.append("  symTableStack=").append(symTableStack).append(",\n");
        sb.append("  out=").append(out).append(",\n");
        sb.append("  fileTable=").append(String.valueOf(fileTable)).append(",\n");
        sb.append("  heap=").append(heap).append(",\n");
        sb.append("  procTable=").append(procTable).append("\n");
        return sb.toString();
    }
}
