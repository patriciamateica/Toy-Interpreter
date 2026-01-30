package model.value;

import model.type.Boolean;
import model.type.Type;

public class BooleanValue implements Value {
    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public Type getType() {
        return new Boolean();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public Value deepCopy(){
        return new BooleanValue(value);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof BooleanValue){
            return this.value == ((BooleanValue) o).value;
        }
        return false;
    }
}
