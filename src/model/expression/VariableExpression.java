package model.expression;

import exceptions.MyException;
import exceptions.UndefinedVariableException;
import model.adt.heap.IHeap;
import model.adt.map.IMap;
import model.type.Integer;
import model.type.Type;
import model.value.Value;

public class VariableExpression implements Expression<Value> {
    private final String variableName;

    public VariableExpression(String variableName) {

        this.variableName = variableName;
    }

    @Override
    public Value evaluate(IMap symTable, IHeap heap) {
        if (!symTable.isDefined(variableName)) {
            throw new UndefinedVariableException("variable " + variableName + " is not defined");
        }
        return (Value) symTable.getValue(variableName);
    }
    /*
    * - retrieve the value of the variable from the symbol table.
    * - returns the value from the symbol table.
     */
    @Override
    public Type typecheck(IMap<String, Type> typeEnv) throws MyException {
       return typeEnv.getValue(variableName);
    }

    @Override
    public String toString() {
        return variableName;
    }
}