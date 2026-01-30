package model.value;

import model.type.Integer;
import model.type.Type;

public class IntegerValue implements Value {
    private final int value;

    public IntegerValue(int value) {

        this.value = value;
    }

    public int value() {

        return value;
    }

    @Override
    public Type getType() {

        return new Integer();
    }

    @Override
    public String toString() {

        return String.valueOf(value);
    }

    @Override
    public Value deepCopy(){
        return new IntegerValue(value);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof IntegerValue){
            return this.value == ((IntegerValue) o).value;
        }
        return false;
    }
}
