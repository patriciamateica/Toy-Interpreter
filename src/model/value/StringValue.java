package model.value;

import model.type.StringType;
import model.type.Type;

public class StringValue implements Value{
    private final String value;
    public StringValue(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof StringValue){
            return this.value.equals(((StringValue) o).value);
        }
        return false;
    }

    @Override
    public Type getType(){
        return new StringType();
    }

    @Override
    public Value deepCopy(){
        return new StringValue(value);
    }

    public String toString(){ return value;}

}
