package model.statement;

import exceptions.MyException;
import model.adt.barrier.IBarrier;
import model.adt.map.IMap;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

import java.util.ArrayList;

public class NewBarrierStatement implements Statement {
    private final String var;
    private final Expression exp;

    public NewBarrierStatement(String var, Expression exp) {
        this.var = var;
        this.exp = exp;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        // Evaluate the expression
        Value value = (Value) exp.evaluate(state.getSymTable(), state.getHeap());

        // Check if the result is an integer
        if (!(value instanceof IntegerValue)) {
            throw new MyException("Expression must evaluate to an integer");
        }

        int nr = ((IntegerValue) value).value();

        // Get the barrier table
        IBarrier barrierTable = state.getBarrierTable();

        // Allocate a new barrier (thread-safe operation)
        int newFreeLocation = barrierTable.allocate(nr, new ArrayList<>());

        // Check if var exists in symbol table and has type int
        if (!state.getSymTable().isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined");
        }

        Value varValue = (Value) state.getSymTable().getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        // Update symbol table with the new barrier address
        state.getSymTable().update(var, new IntegerValue(newFreeLocation));

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        // Check if expression type is int
        Type expType = exp.typecheck(typeEnv);
        if (!(expType instanceof Integer)) {
            throw new MyException("NewBarrier: expression must be of type int");
        }

        // Check if variable is defined and is of type int
        if (!typeEnv.isDefined(var)) {
            throw new MyException("NewBarrier: variable " + var + " is not defined");
        }

        Type varType = (Type) typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("NewBarrier: variable " + var + " must be of type int");
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "newBarrier(" + var + ", " + exp + ")";
    }
}
