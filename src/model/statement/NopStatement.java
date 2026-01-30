// java
package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.state.ProgramState;
import model.type.Type;

/**
 * nop
 * does nothing when executed; typechecks to the same environment
 */
public class NopStatement implements Statement {

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        // do nothing
        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        // no changes to the type environment
        return typeEnv;
    }

    @Override
    public String toString() {
        return "nop";
    }
}
