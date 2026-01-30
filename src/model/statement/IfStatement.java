package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Type;
import model.value.BooleanValue;
import model.value.Value;
import model.type.Boolean;

public class IfStatement implements Statement {
    private final Expression<Value> condition;
    private final Statement thenBranch;
    private final Statement elseBranch;

    public IfStatement(Expression<Value> condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Value result = condition.evaluate(state.getSymTable(), state.getHeap());
        if (result instanceof BooleanValue booleanValue) {
            if (booleanValue.value()) {
                state.getExeStack().push(thenBranch);
            } else {
                state.getExeStack().push(elseBranch);
            }
        } else {
            throw new MyException("Condition expression does not evaluate to a boolean.");
        }
        return state;
    }
    /*
     * - evaluate the condition expression in the context of the symbol table.
     * - if the result is true, push the then-branch onto the execution stack.
     * - if the result is false, push the else-branch onto the execution stack.
     * - returns the updated program state.
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type typecond = condition.typecheck(typeEnv);
        if (typecond.equals(new Boolean())) {
            thenBranch.typecheck(typeEnv.deepCopy());
            elseBranch.typecheck(typeEnv.deepCopy());
            return typeEnv;
        } else {
            throw new MyException("Condition expression does not evaluate to a boolean.");
        }
    }

    @Override
    public String toString() {
        return "(IF(" + condition + ") THEN(" + thenBranch + ") ELSE(" + elseBranch + "))";
    }
}