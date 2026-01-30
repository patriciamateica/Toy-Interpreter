package model.statement;

import exceptions.MyException;
import model.state.ProgramState;
import model.type.Type;
import model.adt.map.IMap;
import model.value.Value;

public class VariableDeclarationStatement implements Statement {
    private final Type type;
    private final String variableName;

    public VariableDeclarationStatement(Type type, String variableName) {
        this.type = type;
        this.variableName = variableName;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap symbolTable = state.getSymTable();
        if (symbolTable.isDefined(variableName)) {
            throw new MyException("Variable already defined");
        }

        Value defaultValue = type.getDefaultValue();
        symbolTable.put(variableName, defaultValue);

        return state;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        typeEnv.put(variableName, type);
        return typeEnv;
    }


    @Override
    public String toString() {
        return type + " " + variableName;
    }
}