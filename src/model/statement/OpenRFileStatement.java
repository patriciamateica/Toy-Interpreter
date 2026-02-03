// java
package model.statement;

import exceptions.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.type.Type;
import model.type.StringType;
import model.value.StringValue;
import model.value.Value;
import model.adt.dictfile.FileTable;
import model.adt.map.IMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenRFileStatement implements Statement {
    private final Expression expression;

    public OpenRFileStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Value val = (Value) expression.evaluate(state.getSymTable(), state.getHeap());
        if (!(val instanceof StringValue)) {
            throw new MyException("OpenRFile: expression did not evaluate to a string");
        }

        String fileName = ((StringValue) val).value();
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            throw new MyException("File not found: " + fileName);
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            FileTable fileTable = state.getFileTable();
            if (fileTable.isOpen(fileName)) {
                reader.close();
                throw new MyException("File already opened: " + fileName);
            }
            fileTable.addOpenFile(fileName, reader);
        } catch (IOException e) {
            throw new MyException("Unable to open file `" + fileName + "`: " + e.getMessage());
        }

        return null;
    }

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type exprType = expression.typecheck(typeEnv);
        if (exprType.equals(new StringType())) {
            return typeEnv;
        } else {
            throw new MyException("OpenRFile: expression is not of string type");
        }
    }

    @Override
    public String toString() {
        return "openRFile(" + expression + ")";
    }
}