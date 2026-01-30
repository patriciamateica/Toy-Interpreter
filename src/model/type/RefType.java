package model.type;

import model.value.Value;
import model.value.RefValue;

public class RefType implements Type {
    private final Type inner;

    public RefType(Type inner) {
        this.inner = inner;
    }

    public Type getInner() {
        return inner;
    }

    @Override
    public Value getDefaultValue() {
        return new RefValue(0, inner);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (!(another instanceof RefType)) return false;
        RefType other = (RefType) another;
        return inner.equals(other.getInner());
    }
    /*
    * - returns true if the inner type is a reference type, false otherwise
     */

    @Override
    public String toString() {
        return "Ref(" + inner.toString() + ")";
    }
}