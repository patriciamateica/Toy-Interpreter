package model.statement;

import exceptions.MyException;
import javafx.util.Pair;
import model.adt.map.IMap;
import model.adt.semaphore.ISemaphore;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Integer;
import model.type.Type;
import model.value.IntegerValue;
import model.value.Value;

import java.util.ArrayList;

public class CreateSemaphoreStatement implements Statement {
    private final String var;
    private final Expression exp1;

    public CreateSemaphoreStatement(String var, Expression exp1) {
        this.var = var;
        this.exp1 = exp1;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        ISemaphore semaphoreTable = state.getSemaphoreTable();

        // Check if var exists in SymTable and has type int
        if (!symTable.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the symbol table");
        }

        Value varValue = symTable.getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        // Evaluate exp1
        Value number1Value = (Value) exp1.evaluate(symTable, state.getHeap());

        // Check if number1 is an integer
        if (!(number1Value instanceof IntegerValue)) {
            throw new MyException("Expression " + exp1 + " must evaluate to an integer");
        }

        int number1 = ((IntegerValue) number1Value).value();

        // Get new free location from semaphore table (atomic operation)
        int newFreeLocation = semaphoreTable.getFreeAddress();

        // Create new semaphore entry with (number1, empty list) - atomic operation
        semaphoreTable.put(newFreeLocation, new Pair<>(number1, new ArrayList<>()));

        // Update SymTable with the new location
        symTable.update(var, new IntegerValue(newFreeLocation));

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        // Check if var exists in type environment
        if (!typeEnv.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the type environment");
        }

        // Check if var has type int
        Type varType = typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        // Check if exp1 has type int
        Type exp1Type = exp1.typecheck(typeEnv);
        if (!(exp1Type instanceof Integer)) {
            throw new MyException("Expression " + exp1 + " must be of type int");
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "createSemaphore(" + var + ", " + exp1.toString() + ")";
    }
}
