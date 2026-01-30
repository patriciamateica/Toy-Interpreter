package model.value;

import model.type.RefType;
import model.type.Type;

public class RefValue implements Value {
    private final int address;
    private final Type locationType;

    public RefValue(int address, Type locationType) {
        this.address = address;
        this.locationType = locationType;
    }

    public int getAddress() {
        return address;
    }

    public Type getLocationType() {
        return locationType;
    }

    @Override
    public Type getType() {
        return new RefType(locationType);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (!(another instanceof RefValue)) return false;
        RefValue o = (RefValue) another;
        return this.address == o.address && this.locationType.equals(o.locationType);
    }
    /*
    * - returns a string representation of the value in the format (address, locationType).
     */

    @Override
    public Value deepCopy(){
        return new RefValue(address, locationType);
    }

    @Override
    public String toString() {
        return "(" + address + "," + locationType + ")";
    }
}