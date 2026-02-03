package model.state;

import exceptions.MyException;
import model.adt.dictfile.FileTable;
import model.adt.stack.IStack;
import model.adt.map.IMap;
import model.adt.list.IList;
import model.statement.Statement;
import model.value.Value;
import model.adt.heap.IHeap;
import model.adt.barrier.IBarrier;

public class ProgramState {
    private IStack exeStack;
    private IMap symTable;
    private IList<Value> out;
    private FileTable fileTable;
    private Statement originalProgram;
    private IHeap heap;
    private IBarrier barrierTable;
    private final int id;
    private static int lastId = 0;

    public ProgramState(IStack stk, IMap symtbl, IList<Value> ot, FileTable fileTable, Statement prg, IHeap heap, IBarrier barrierTable, int ignoredId) {
        this.exeStack = stk;
        this.symTable = symtbl;
        this.out = ot;
        this.fileTable = fileTable;
        this.originalProgram = prg;
        if (this.originalProgram != null) {
            this.exeStack.push(this.originalProgram);
        }
        this.heap = heap;
        this.barrierTable = barrierTable;
        this.id = nextId();
    }
    //added the new barrier table to the ProgramState constructor

    public ProgramState(IStack stk, IMap symtbl, IList<Value> ot, FileTable fileTable, IHeap heap, IBarrier barrierTable, int ignoredId) {
        this(stk, symtbl, ot, fileTable, null, heap, barrierTable, ignoredId);
    }

    public IStack getExeStack() {
        return exeStack;
    }

    public IMap getSymTable() {
        return symTable;
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

    public void setSymTable(IMap symTable) {
        this.symTable = symTable;
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

    public IBarrier getBarrierTable() {
        return barrierTable;
    }

    public void setBarrierTable(IBarrier barrierTable) {
        this.barrierTable = barrierTable;
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
        sb.append("  symTable=").append(symTable).append(",\n");
        sb.append("  out=").append(out).append(",\n");
        sb.append("  fileTable=").append(String.valueOf(fileTable)).append(",\n");
        sb.append("  heap=").append(heap).append(",\n");
        sb.append("  barrierTable=").append(barrierTable).append("\n");
        return sb.toString();
    }
}
