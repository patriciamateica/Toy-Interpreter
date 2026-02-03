package model.statement;

import exceptions.MyException;
import model.adt.map.IMap;
import model.adt.map.MapSymbolTable;
import model.adt.procedures.IProcTable;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Type;
import model.value.Value;

import java.util.List;

public class CallStatement implements Statement {
    private final String procName;
    private final List<Expression<Value>> arguments;

    public CallStatement(String procName, List<Expression<Value>> arguments) {
        this.procName = procName;
        this.arguments = arguments;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IProcTable procTable = state.getProcTable();

        if (!procTable.isDefined(procName)) {
            throw new MyException("Procedure " + procName + " is not defined");
        }

        List<String> formalParams = procTable.getParams(procName);
        Statement body = procTable.getBody(procName);

        if (formalParams.size() != arguments.size()) {
            throw new MyException("Procedure " + procName + " expects " +
                    formalParams.size() + " arguments but got " + arguments.size());
        }

        IMap<String, Value> currentSymTable = state.getSymTable();

        List<Value> evaluatedArgs = arguments.stream()
                .map(exp -> exp.evaluate(currentSymTable, state.getHeap()))
                .toList();

        IMap<String, Value> newSymTable = new MapSymbolTable<>();
        for (int i = 0; i < formalParams.size(); i++) {
            newSymTable.put(formalParams.get(i), evaluatedArgs.get(i));
        }

        state.pushSymTable(newSymTable);

        state.getExeStack().push(new ReturnStatement());
        state.getExeStack().push(body);

        return null;
    }
    /*
     * handles the context switch from "Main" to "Procedure".
     * evaluates arguments in the CURRENT scope.
     * creates a NEW scope (SymbolTable) for the procedure.
     * maps parameters to values in that new scope.
     * pushes the body of the procedure onto the execution stack.
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        return typeEnv;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("call ").append(procName).append("(");
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(arguments.get(i).toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
