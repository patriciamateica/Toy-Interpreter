package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.state.ProgramState;
import model.type.Type;

public class CompoundStatement implements Statement {
    private final Statement first;
    private final Statement second;

    public CompoundStatement(Statement first, Statement second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        var stack = state.getExeStack();
        stack.push(second);
        stack.push(first);
        return state;
    }
    /*
    * - pushes onto the stack two statements.
    * - the second statement is executed first.
    * - the first statement is executed second.
    * - returns the updated program state.
     */
    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException{
        return second.typecheck(first.typecheck(typeEnv));
    }

    @Override
    public String toString() {

        return "(" + first + "; " + second + ")";
    }
}
