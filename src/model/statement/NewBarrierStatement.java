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

//initializes a synchronization barrier with a given size
public class NewBarrierStatement implements Statement {
    private final String var;
    private final Expression exp;

    public NewBarrierStatement(String var, Expression exp) {
        this.var = var;
        this.exp = exp;
    }


    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Value value = (Value) exp.evaluate(state.getSymTable(), state.getHeap());

        if (!(value instanceof IntegerValue)) {
            throw new MyException("Expression must evaluate to an integer");
        }

        int nr = ((IntegerValue) value).value();

        IBarrier barrierTable = state.getBarrierTable();

        int newFreeLocation = barrierTable.allocate(nr, new ArrayList<>());

        if (!state.getSymTable().isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined");
        }

        Value varValue = (Value) state.getSymTable().getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        state.getSymTable().update(var, new IntegerValue(newFreeLocation));

        return null;
    }
    /*
     * evaluates the expression to find N (barrier limit)
     * allocates a new slot in the BarrierTable
     * updates 'var' in the SymbolTable to point to this new barrier index.
   */


    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type expType = exp.typecheck(typeEnv);
        if (!(expType instanceof Integer)) {
            throw new MyException("NewBarrier: expression must be of type int");
        }

        if (!typeEnv.isDefined(var)) {
            throw new MyException("NewBarrier: variable " + var + " is not defined");
        }

        Type varType = (Type) typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("NewBarrier: variable " + var + " must be of type int");
        }

        return typeEnv;
    }
    /*
     * ensures 'exp' evaluates to int and 'var' is of type int BEFORE execution
     */


    @Override
    public String toString() {
        return "newBarrier(" + var + ", " + exp + ")";
    }
}
