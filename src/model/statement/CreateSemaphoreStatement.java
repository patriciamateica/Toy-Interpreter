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

        if (!symTable.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the symbol table");
        }

        Value varValue = symTable.getValue(var);
        if (!(varValue.getType() instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }

        Value number1Value = (Value) exp1.evaluate(symTable, state.getHeap());

        if (!(number1Value instanceof IntegerValue)) {
            throw new MyException("Expression " + exp1 + " must evaluate to an integer");
        }

        int number1 = ((IntegerValue) number1Value).value();

        int newFreeLocation = semaphoreTable.getFreeAddress();

        semaphoreTable.put(newFreeLocation, new Pair<>(number1, new ArrayList<>()));

        symTable.update(var, new IntegerValue(newFreeLocation));

        return null;
    }
    /*
     *gets the symbol table (variables) and semaphore table from the program state
     *validates variable: checks that var exists in the symbol table and is of type int
     *evaluates exp1 to get the initial semaphore capacity (must be an IntegerValue)
     *gets the integer value from the evaluated expression - this becomes the maximum number of threads that can access the semaphore
     *gets a free location in the semaphore table and stores a new semaphore entry containing:
     *the capacity is number 1 and an empty list that tracks which threads are using the semaphore
     */


    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        if (!typeEnv.isDefined(var)) {
            throw new MyException("Variable " + var + " is not defined in the type environment");
        }

        Type varType = typeEnv.getValue(var);
        if (!(varType instanceof Integer)) {
            throw new MyException("Variable " + var + " must be of type int");
        }
        Type exp1Type = exp1.typecheck(typeEnv);
        if (!(exp1Type instanceof Integer)) {
            throw new MyException("Expression " + exp1 + " must be of type int");
        }

        return typeEnv;
    }
    //var must be defined in the type environment and must be of type int. exp1 must be of type int.

    @Override
    public String toString() {
        return "createSemaphore(" + var + ", " + exp1.toString() + ")";
    }
}
