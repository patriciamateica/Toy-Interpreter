package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.procedures.IProcTable;
import model.state.ProgramState;
import model.type.Type;

import java.util.List;

public class CreateProcedureStatement implements Statement {
    private final String procName;
    private final List<String> params;
    private final Statement body;

    public CreateProcedureStatement(String procName, List<String> params, Statement body) {
        this.procName = procName;
        this.params = params;
        this.body = body;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IProcTable procTable = state.getProcTable();

        if (procTable.isDefined(procName)) {
            throw new MyException("Procedure " + procName + " already defined");
        }

        procTable.add(procName, params, body);
        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        return typeEnv;
    }

    @Override
    public String toString() {
        return "procedure " + procName + "(" + String.join(", ", params) + ") " + body.toString();
    }
}
