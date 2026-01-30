package model.statement;

import exceptions.MyException;
import exceptions.UndefinedVariableException;
import model.expression.Expression;
import model.state.ProgramState;
import model.adt.map.IMap;
import model.type.Type;
import model.value.Value;

public class AssignmentStatement implements Statement {
    private final Expression<Value> expression;
    private final String variableName;

    public AssignmentStatement(Expression<Value> expression, String variableName) {
        this.expression = expression;
        this.variableName = variableName;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symbolTable = state.getSymTable();
        if (!symbolTable.isDefined(variableName)) {
            throw new UndefinedVariableException("Variable '" + variableName + "' is not declared");
        }

        Value value = expression.evaluate(symbolTable, state.getHeap());

        Value currentVarValue = symbolTable.getValue(variableName);
        if (!value.getType().equals(currentVarValue.getType())) {
            throw new MyException("Type mismatch: variable '" + variableName + "' has type "
                    + currentVarValue.getType() + " but expression has type " + value.getType());
        }

        symbolTable.update(variableName, value);
        return state;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type typevar = typeEnv.getValue(variableName);
        Type typeexp = expression.typecheck(typeEnv);
        if (typevar.equals(typeexp)) {
            return typeEnv;
        } else {
            throw new MyException("Type mismatch: variable '" + variableName + "' has type " + typevar + " but expression has type " + typeexp);
        }
    }
    @Override
    public String toString() {
        return variableName + " = " + expression;
    }
}