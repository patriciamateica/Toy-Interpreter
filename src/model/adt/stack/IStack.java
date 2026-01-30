package model.adt.stack;

import model.statement.Statement;

public interface IStack {
    void push(Statement stmt);
    Statement pop();
    Statement peek();
    boolean isEmpty();
    int size();
}
