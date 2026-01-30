package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.expression.Expression;
import model.expression.ValueExpression;
import model.value.Value;
import model.value.IntegerValue;
import model.type.Type;
import model.type.Integer;
import model.adt.stack.IStack;
import model.adt.map.IMap;
import model.adt.heap.IHeap;

/**
 * sleep(number)
 * execution:
 *  - pop the statement (handled by ProgramState.oneStep)
 *  - evaluate number; if number == 0 do nothing
 *    else push sleep(number-1) on the exeStack
 */
public class SleepStatement implements Statement {
    private final Expression<Value> numberExp;

    public SleepStatement(Expression<Value> numberExp) {
        this.numberExp = numberExp;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IStack exeStack = state.getExeStack();
        IMap<String, Value> symTable = state.getSymTable();
        IHeap<Value> heap = state.getHeap();

        Value v = numberExp.evaluate(symTable, heap);
        if (!(v instanceof IntegerValue)) {
            throw new MyException("Sleep: argument is not an integer");
        }

        int n = ((IntegerValue) v).value();
        if (n > 0) {
            // push sleep(n-1) as a literal-decremented sleep
            exeStack.push(new SleepStatement(new ValueExpression(new IntegerValue(n - 1))));
        }

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type t = numberExp.typecheck(typeEnv);
        if (!t.equals(new Integer())) {
            throw new MyException("Sleep: argument expression must be of type int");
        }
        return typeEnv;
    }

    @Override
    public String toString() {
        return String.format("sleep(%s)", numberExp);
    }
}
