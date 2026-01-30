package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Type;
import model.value.Value;

public class PrintStatement implements Statement {
    private final Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Value val = (Value) expression.evaluate(state.getSymTable(), state.getHeap());
        if (val == null) {
            throw new MyException("Print: evaluated expression is null");
        }
        state.getOut().add(val);

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException{
        expression.typecheck(typeEnv);
        return typeEnv;
    }

    @Override
    public String toString() {
        return "print " + expression;
    }
}