// java
package model.statement;

import exceptions.FileNotOpenException;
import exceptions.InvalidTypeException;
import exceptions.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.adt.map.IMap;
import model.type.Type;
import model.type.StringType;
import model.value.Value;
import model.value.StringValue;

import java.io.BufferedReader;
import java.io.IOException;

public class CloseRFileStatement implements Statement {
    private final Expression expression;

    public CloseRFileStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Object evalResult = expression.evaluate(state.getSymTable(), state.getHeap());
        if (!(evalResult instanceof Value)) {
            throw new MyException("CloseRFile: expression did not evaluate to a Value");
        }
        Value val = (Value) evalResult;
        if (!(val instanceof StringValue)) {
            throw new InvalidTypeException("CloseRFile: expression is not a string");
        }
        String fileName = ((StringValue) val).value();
        if (!state.getFileTable().isOpen(fileName)) {
            throw new FileNotOpenException(fileName);
        }

        BufferedReader br = state.getFileTable().getOpenFile(fileName);
        try {
            if (br != null) {
                br.close();
            }
            state.getFileTable().closeFile(fileName);
        } catch (IOException e) {
            throw new MyException("CloseRFile: error closing file: " + e.getMessage());
        }

        return state;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type exprType = expression.typecheck(typeEnv);
        if (!exprType.equals(new StringType())) {
            throw new MyException("CloseRFile: expression is not of type string");
        }
        return typeEnv;
    }

    @Override
    public String toString() {
        return "closeRFile(" + expression + ")";
    }
}