package model.type;

import model.value.IntegerValue;
import model.value.Value;

public class Integer implements Type {
    @Override
    public boolean equals(Object another) {
        return another instanceof Integer; //it will return true if the object is an instance of Integer class, or false otherwise
    }

    @Override
    public String toString() {

        return "int";
    }

    @Override
    public Value getDefaultValue() {

        return new IntegerValue(0);
    }
}
