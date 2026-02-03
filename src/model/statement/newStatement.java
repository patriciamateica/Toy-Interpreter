package model.statement;

import exceptions.InvalidTypeException;
import exceptions.MyException;
import exceptions.UndefinedVariableException;
import model.state.ProgramState;
import model.adt.map.IMap;
import model.adt.heap.IHeap;
import model.expression.Expression;
import model.value.Value;
import model.value.RefValue;
import model.type.RefType;
import model.type.Type;

public class newStatement implements Statement {
    private final String varName;
    private final Expression expr;

    public newStatement(String varName, Expression expr) {
        this.varName = varName;
        this.expr = expr;
    }

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        IMap<String, Value> symTable = state.getSymTable();
        IHeap heap = state.getHeap();

        if (!symTable.isDefined(varName)) {
            throw new UndefinedVariableException("new: variable not defined: " + varName);
        }

        Value varValue = (Value) symTable.getValue(varName);

        if (!(varValue.getType() instanceof RefType)) {
            throw new InvalidTypeException("new: variable " + varName + " is not of RefType");
        }

        Value evaluated = (Value) expr.evaluate(symTable, heap);

        RefValue refVal = (RefValue) varValue;
        if (!evaluated.getType().equals(refVal.getLocationType())) {
            throw new InvalidTypeException("new: type mismatch - expression type " + evaluated.getType()
                    + " does not match location type " + refVal.getLocationType());
        }

        int newAddr = heap.allocate(evaluated);
        symTable.update(varName, new RefValue(newAddr, refVal.getLocationType()));

        return state;
    }

    /*
     * - evaluate the expression in the context of the symbol table.
     * - allocate a new value in the heap using the result of the expression as its value.
     */

    @Override
    public IMap<String, Type> typecheck(IMap<String, Type> typeEnv) throws MyException {
        Type typeVar = typeEnv.getValue(varName);
        Type typeExp = expr.typecheck(typeEnv);
        if (typeVar.equals(new RefType(typeExp)))
            return typeEnv;
        else
            throw new MyException("NEW stmt: right hand side and left hand side have different types");
    }

    @Override
    public String toString() {
        return "new(" + varName + ", " + expr + ")";
    }
}