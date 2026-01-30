package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.expression.Expression;
import model.value.Value;
import model.type.Type;
import model.adt.map.IMap;
import model.adt.stack.IStack;
import model.adt.heap.IHeap;

/**
 * switch(exp) (case exp1: stmt1) (case exp2: stmt2) (default: stmt3)
 */
public class SwitchStatement implements Statement {
    private final Expression<Value> exp;
    private final Expression<Value> exp1;
    private final Expression<Value> exp2;
    private final Statement stmt1;
    private final Statement stmt2;
    private final Statement stmt3;

    public SwitchStatement(Expression<Value> exp, Expression<Value> exp1, Expression<Value> exp2,
                           Statement stmt1, Statement stmt2, Statement stmt3) {
        this.exp = exp;
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
        this.stmt3 = stmt3;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        IHeap<Value> heap = state.getHeap();

        Value v  = exp.evaluate(symTable, heap);
        Value v1 = exp1.evaluate(symTable, heap);
        Value v2 = exp2.evaluate(symTable, heap);

        IStack exeStack = state.getExeStack();
        if (v.equals(v1)) {
            exeStack.push(stmt1);
        } else if (v.equals(v2)) {
            exeStack.push(stmt2);
        } else {
            exeStack.push(stmt3);
        }

        return null; // common pattern in this project: statements return null unless they create a new ProgramState
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type tExp  = exp.typecheck(typeEnv);
        Type tExp1 = exp1.typecheck(typeEnv);
        Type tExp2 = exp2.typecheck(typeEnv);

        if (!tExp.equals(tExp1) || !tExp.equals(tExp2)) {
            throw new MyException("Switch: the three expressions must have the same type");
        }

        // typecheck each branch on a copy of the environment
        stmt1.typecheck(typeEnv.deepCopy());
        stmt2.typecheck(typeEnv.deepCopy());
        stmt3.typecheck(typeEnv.deepCopy());

        return typeEnv;
    }

    @Override
    public String toString() {
        return String.format("switch(%s) (case %s: %s) (case %s: %s) (default: %s)",
                exp, exp1, stmt1, exp2, stmt2, stmt3);
    }
}
