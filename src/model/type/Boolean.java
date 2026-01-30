package model.type;

import model.value.BooleanValue;
import model.value.Value;

public class Boolean implements Type {
    @Override
    public boolean equals(Object another) {

        return another instanceof Boolean;
    }

    @Override
    public String toString() {

        return "bool";
    }

    @Override
    public Value getDefaultValue() {

        return new BooleanValue(false);
    }
}
