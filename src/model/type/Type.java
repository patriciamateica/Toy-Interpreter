package model.type;
import model.value.Value;

public interface Type {
    Value getDefaultValue();
    boolean equals(Object another);
}
