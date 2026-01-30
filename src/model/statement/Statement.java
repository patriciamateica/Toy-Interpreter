package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.state.ProgramState;
import model.type.Type;

public interface Statement {
    ProgramState execute(ProgramState state) throws MyException;
    IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException;
}
