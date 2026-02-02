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

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        return typeEnv;
    }

    @Override
    public String toString() {
        return "return";
    }
}
