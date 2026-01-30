package model.adt.stack;

import exceptions.ExecutionStackEmptyException;
import model.statement.Statement;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Iterator;

public class ExecutionStack implements IStack {
    private final Deque<Statement> stack;

    public ExecutionStack() {

        this.stack = new LinkedList<>();
    }

    @Override
    public void push(Statement stmt) {

        stack.push(stmt);
    }

    @Override
    public Statement pop() {
        if (stack.isEmpty()) {
            throw new ExecutionStackEmptyException("Execution stack is empty");
        }
        return stack.pop();
    }

    @Override
    public Statement peek() {
        if (stack.isEmpty()) {
            throw new ExecutionStackEmptyException("Execution stack is empty");
        }
        return stack.peek();
    }
    /*
    * - returns true if the stack is empty, false otherwise.
     */

    @Override
    public boolean isEmpty() {

        return stack.isEmpty();
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExeStack[");
        Iterator<Statement> it = stack.iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(it.next().toString());
        }
        sb.append("]");
        return sb.toString();
    }
}