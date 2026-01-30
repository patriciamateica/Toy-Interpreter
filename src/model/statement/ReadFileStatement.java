package model.statement;

import exceptions.CannotReadFileException;
import exceptions.FileNotOpenException;
import exceptions.InvalidTypeException;
import exceptions.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Type;
import model.type.StringType;
import model.type.Integer;
import model.value.IntegerValue;
import model.value.StringValue;
import model.adt.map.IMap;

import java.io.BufferedReader;

public class ReadFileStatement implements Statement {
    private final Expression expression;
    private final String varName;

    public ReadFileStatement(Expression expression, String varName) {
        this.expression = expression;
        this.varName = varName;
    }

    @Override
    public ProgramState execute(ProgramState state) {
        if (!state.getSymTable().isDefined(varName)) {
            throw new InvalidTypeException(varName);
        }

        var varValue = state.getSymTable().getValue(varName);
        if (!(varValue instanceof IntegerValue)) {
            throw new InvalidTypeException(varName);
        }

        var value = expression.evaluate(state.getSymTable(), state.getHeap());
        if (!(value instanceof StringValue)) {
            throw new InvalidTypeException("Type must be String");
        }
        String fileName = ((StringValue) value).value();

        if (!state.getFileTable().isOpen(fileName)) {
            throw new FileNotOpenException(fileName);
        }
        BufferedReader br = state.getFileTable().getOpenFile(fileName);
        String line;
        try {
            line = br.readLine();
        } catch (Exception e) {
            throw new CannotReadFileException("Error reading file");
        }
        if (line == null) {
            state.getSymTable().update(varName, new IntegerValue(0));
        } else {
            state.getSymTable().update(varName, new IntegerValue(java.lang.Integer.parseInt(line)));
        }
        return state;
    }
    /*
     * - evaluate the expression in the context of the symbol table.
     * - ensure the file is open.
     * - read the next line from the file.
     * - if the line is null, set the variable to 0.
     * - if the line is not null, set the variable to the integer value of the line.
     * - returns the updated program state.
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type exprType = expression.typecheck(typeEnv);
        if (!exprType.equals(new StringType())) {
            throw new MyException("ReadFile: expression is not a string");
        }

        Type varType = typeEnv.getValue(varName);
        if (varType == null) {
            throw new MyException("ReadFile: variable " + varName + " is not declared");
        }
        if (!varType.equals(new Integer())) {
            throw new MyException("ReadFile: variable " + varName + " is not of type int");
        }

        return typeEnv;
    }


    @Override
    public String toString() {
        return "readFile(" + expression + ", " + varName + ")";
    }
}