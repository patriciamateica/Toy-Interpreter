package model.adt.stack;

import model.statement.Statement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ExecutionStack implements IStack {
    private final Deque<Statement> stack;

    public ExecutionStack() {
        this.stack = new ArrayDeque<>();
    }

    @Override
    public void push(Statement stmt) {
        stack.push(stmt);
    }

    @Override
    public Statement pop() {
        return stack.pop();
    }

    @Override
    public Statement peek() {
        return stack.peek();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>(stack);
    }
}
