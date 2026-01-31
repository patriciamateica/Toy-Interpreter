package model.adt.stack;

import model.statement.Statement;

import java.util.List;

public interface IStack {
    void push(Statement stmt);
    Statement pop();
    Statement peek();
    boolean isEmpty();
    int size();
    List<Statement> getStatements();
}
