package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.state.ProgramState;
import model.type.Type;

public class ReturnStatement implements Statement {
    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        state.popSymTable();
        return null;
    }
    /*
     * handles the "exit" from a procedure.
     * accesses the stack of Symbol Tables in ProgramState.
     * pops the top table (the specific scope of the procedure that just finished).
     * this restores the Symbol Table to the state of the 'caller'.
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        return typeEnv;
    }

    @Override
    public String toString() {
        return "return";
    }
}
