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
 * wait(number)
 * execution:
 *  - pop the statement (handled by ProgramState.oneStep)
 *  - evaluate number; if number == 0 do nothing
 *    else push (print(number); wait(number-1)) on the exeStack
 */
public class WaitStatement implements Statement {
    private final Expression<Value> numberExp;

    public WaitStatement(Expression<Value> numberExp) {
        this.numberExp = numberExp;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IStack exeStack = state.getExeStack();
        IMap<String, Value> symTable = state.getSymTable();
        IHeap<Value> heap = state.getHeap();

        Value v = numberExp.evaluate(symTable, heap);
        if (!(v instanceof IntegerValue)) {
            throw new MyException("Wait: argument is not an integer");
        }

        int n = ((IntegerValue) v).value();
        if (n > 0) {
            Statement printN = new PrintStatement(new ValueExpression(new IntegerValue(n)));
            Statement waitNminus1 = new WaitStatement(new ValueExpression(new IntegerValue(n - 1)));
            exeStack.push(new CompoundStatement(printN, waitNminus1));
        }

        return null;
    }
    /*
    *step 1: evaluate the expression
    *validation: must be an integer
    *step 2: extract the integer value (N)
    *step 3: recursive Logic
    *create statement: print(n)
    *create statement: wait(n - 1)
    *combine them: print(n); wait(n-1)
    *we push this compound statement to the stack to be executed next.
    *implicit else: If n <= 0, we do nothing. The statement is removed from stack, recursion ends.
    */
    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type t = numberExp.typecheck(typeEnv);
        if (!t.equals(new Integer())) {
            throw new MyException("Wait: argument expression must be of type int");
        }
        return typeEnv;
    }

    @Override
    public String toString() {
        return String.format("wait(%s)", numberExp);
    }
}
