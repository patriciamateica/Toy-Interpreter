package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.expression.Expression;
import model.expression.VariableExpression;
import model.expression.RelationalExpression;
import model.value.Value;
import model.type.Type;
import model.type.Integer;
import model.adt.map.IMap;
import model.adt.stack.IStack;
import model.adt.heap.IHeap;

/**
 * for(v=exp1; v<exp2; v=exp3) stmt
 * is converted to:
 * int v; v=exp1; (while(v<exp2) stmt; v=exp3)
 */
public class ForStatement implements Statement {
    private final String varName;
    private final Expression<Value> exp1;
    private final Expression<Value> exp2;
    private final Expression<Value> exp3;
    private final Statement body;

    public ForStatement(String varName, Expression<Value> exp1, Expression<Value> exp2, Expression<Value> exp3, Statement body) {
        this.varName = varName;
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.exp3 = exp3;
        this.body = body;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IStack exeStack = state.getExeStack();

        // convert: int v; v=exp1; (while(v<exp2) { stmt; v=exp3 })
        Statement decl = new VariableDeclarationStatement(new Integer(), varName);
        Statement init = new AssignmentStatement(exp1, varName);
        // condition: v < exp2
        Expression<Value> condition = new RelationalExpression("<", new VariableExpression(varName), exp2);
        // loop body: stmt; v=exp3
        Statement loopBody = new CompoundStatement(body, new AssignmentStatement(exp3, varName));
        Statement whileStmt = new WhileStatement(condition, loopBody);

        Statement desugared = new CompoundStatement(decl, new CompoundStatement(init, whileStmt));
        exeStack.push(desugared);
        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        IMap<String, Type> envWithV = typeEnv.deepCopy();
        envWithV.put(varName, new Integer());

        Type t1 = exp1.typecheck(envWithV);
        Type t2 = exp2.typecheck(envWithV);
        Type t3 = exp3.typecheck(envWithV);

        if (!t1.equals(new Integer()) || !t2.equals(new Integer()) || !t3.equals(new Integer()))
            throw new MyException("For: all three expressions must be of type int");
        body.typecheck(envWithV);

        return typeEnv;
    }

    @Override
    public String toString() {
        return String.format("for(%s=%s;%s<%s;%s=%s) %s", varName, exp1, varName, exp2, varName, exp3, body);
    }
}
