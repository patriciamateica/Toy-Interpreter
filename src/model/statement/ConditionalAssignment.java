package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.expression.Expression;
import model.value.Value;
import model.type.Type;
import model.type.Boolean;
import model.adt.map.IMap;
import model.adt.stack.IStack;

public class ConditionalAssignment implements Statement {
    private final String varName;
    private final Expression<Value> cond;
    private final Expression<Value> expTrue;
    private final Expression<Value> expFalse;

    public ConditionalAssignment(String varName, Expression<Value> cond,
                                 Expression<Value> expTrue, Expression<Value> expFalse) {
        this.varName = varName;
        this.cond = cond;
        this.expTrue = expTrue;
        this.expFalse = expFalse;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IStack exeStack = state.getExeStack();
        Statement thenAssign = new AssignmentStatement(expTrue, varName);
        Statement elseAssign = new AssignmentStatement(expFalse, varName);
        Statement desugared = new IfStatement(cond, thenAssign, elseAssign);

        exeStack.push(desugared);
        return null;
    }
    /*
     * this method converts the conditional assignment statement into an if-then-else statement
     * and pushes it onto the execution stack.
     * instead of manually evaluating the condition and updating the table,
     * we construct an equivalent 'IfStatement' that contains two 'AssignmentStatements'.
     * we push this new statement to the stack to be executed next.
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type tCond = cond.typecheck(typeEnv);
        if (!tCond.equals(new Boolean()))
            throw new MyException("ConditionalAssignment: condition expression must be of type bool");

        Type tTrue = expTrue.typecheck(typeEnv);
        Type tFalse = expFalse.typecheck(typeEnv);
        if (!tTrue.equals(tFalse))
            throw new MyException("ConditionalAssignment: the two alternative expressions must have the same type");

        if (!typeEnv.isDefined(varName))
            throw new MyException("ConditionalAssignment: variable " + varName + " is not declared");

        Type varType = typeEnv.getValue(varName);
        if (!varType.equals(tTrue))
            throw new MyException("ConditionalAssignment: variable and expression types do not match");

        return typeEnv;
    }

    @Override
    public String toString() {
        return String.format("%s = %s ? %s : %s", varName, cond, expTrue, expFalse);
    }
}
