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
 * converts to:
 * stmt; (while(!exp) stmt)
 */
public class RepeatUntilStatement implements Statement {
    private final Statement stmt;
    private final Expression<Value> exp;

    public RepeatUntilStatement(Statement stmt, Expression<Value> exp) {
        this.stmt = stmt;
        this.exp = exp;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IStack exeStack = state.getExeStack();

        // convert: stmt; (while(!exp) stmt)
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
        stmt.typecheck(typeEnv.deepCopy());
        return typeEnv;
    }

    @Override
    public String toString() {
        return String.format("repeat %s until %s", stmt, exp);
    }
}
