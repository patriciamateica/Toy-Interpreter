package model.state;

import exceptions.MyException;
import model.adt.dictfile.FileTable;
import model.adt.heap.IHeap;
import model.adt.latch.ILatch;
import model.adt.list.IList;
import model.adt.map.IMap;
import model.adt.stack.IStack;
import model.statement.Statement;
import model.value.Value;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgramState {
    private final IStack exeStack;
    private final IMap<String, Value> symTable;
    private final IList<Value> out;
    private final FileTable fileTable;
    private final IHeap<Value> heap;
    private final ILatch latchTable;
    private final Statement originalProgram;
    private final int id;
    private static final AtomicInteger nextId = new AtomicInteger(0);

    public ProgramState(IStack stack, IMap<String, Value> symTable, IList<Value> out,
                        FileTable fileTable, Statement prg, IHeap<Value> heap, ILatch latchTable) {
        this.exeStack = stack;
        this.symTable = symTable;
        this.out = out;
        this.fileTable = fileTable;
        this.heap = heap;
        this.latchTable = latchTable;
        this.originalProgram = prg;
        this.exeStack.push(prg);
        this.id = nextId.getAndIncrement();
    }

    public ProgramState(IStack stack, IMap<String, Value> symTable, IList<Value> out,
                        FileTable fileTable, Statement prg, IHeap<Value> heap, ILatch latchTable, int id) {
        this.exeStack = stack;
        this.symTable = symTable;
        this.out = out;
        this.fileTable = fileTable;
        this.heap = heap;
        this.latchTable = latchTable;
        this.originalProgram = prg;
        this.exeStack.push(prg);
        this.id = id;
    }


    public IStack getExeStack() {
        return exeStack;
    }

    public IMap<String, Value> getSymTable() {
        return symTable;
    }

    public IList<Value> getOut() {
        return out;
    }

    public FileTable getFileTable() {
        return fileTable;
    }

    public IHeap<Value> getHeap() {
        return heap;
    }

    public ILatch getLatchTable() {
        return latchTable;
    }

    public int getId() {
        return id;
    }

    public boolean isNotCompleted() {
        return !exeStack.isEmpty();
    }

    public ProgramState oneStep() throws MyException {
        if (exeStack.isEmpty()) {
            throw new MyException("prgstate stack is empty");
        }
        Statement currentStatement = exeStack.pop();
        return currentStatement.execute(this);
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "ExeStack: " + exeStack.toString() + "\n" +
                "SymTable: " + symTable.toString() + "\n" +
                "Out: " + out.toString() + "\n" +
                "FileTable: " + fileTable.toString() + "\n" +
                "Heap: " + heap.toString() + "\n" +
                "LatchTable: " + latchTable.toString() + "\n";
    }
}
