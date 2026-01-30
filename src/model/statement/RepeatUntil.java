package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.expression.Expression;
import model.expression.NotExpression;
import model.value.Value;
import model.type.Type;
import model.type.Boolean;
import model.adt.map.IMap;
import model.adt.stack.IStack;

/**
 * repeat stmt until exp
 * desugars to:
 * stmt; (while(!exp) stmt)
 */
public class RepeatUntil implements Statement {
    private final Statement stmt;
    private final Expression<Value> exp;

    public RepeatUntil(Statement stmt, Expression<Value> exp) {
        this.stmt = stmt;
        this.exp = exp;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IStack exeStack = state.getExeStack();

        // desugar: stmt; (while(!exp) stmt)
        Statement desugared = new CompoundStatement(
                stmt,
                new WhileStatement(new NotExpression(exp), stmt)
        );

        exeStack.push(desugared);
        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type t = exp.typecheck(typeEnv);
        if (!t.equals(new Boolean())) {
            throw new MyException("RepeatUntil: condition expression must be of type bool");
        }

        // typecheck the body (use a copy)
        stmt.typecheck(typeEnv.deepCopy());
        return typeEnv;
    }

    @Override
    public String toString() {
        return String.format("repeat %s until %s", stmt, exp);
    }
}
