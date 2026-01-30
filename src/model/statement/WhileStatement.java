package model.statement;

import exceptions.InvalidTypeException;
import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.stack.IStack;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Boolean;
import model.type.Type;
import model.value.BooleanValue;
import model.value.Value;

public class WhileStatement implements Statement {
    private final Expression expression;
    private final Statement statement;

    public WhileStatement(Expression expression, Statement statement) {
        this.expression = expression;
        this.statement = statement;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IStack exeStack = state.getExeStack();
        // cast because the project's Expression.evaluate currently resolves to Object at compile-time
        Value value = (Value) expression.evaluate(state.getSymTable(), state.getHeap());
        if (!(value instanceof BooleanValue))
            throw new InvalidTypeException("Type mismatch: while condition must be boolean");
        BooleanValue boolValue = (BooleanValue) value;
        if (boolValue.value()) {
            exeStack.push(this);
            exeStack.push(statement);
        }
        return state;
    }

    /*
     * - evaluate the condition expression
     * - if the result is true, push the while-statement onto the execution stack
     * - returns the updated program state.
     */
    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type condType = expression.typecheck(typeEnv);
        if (condType.equals(new Boolean())) {
            statement.typecheck(typeEnv.deepCopy());
            return typeEnv;
        } else {
            throw new MyException("While condition does not evaluate to a boolean.");
        }
    }


    @Override
    public String toString() {
        return "WhileStatement(condition=" + expression + ", body=" + statement + "\n)";
    }
}