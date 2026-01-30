package exceptions;

public class TypeCheckFailedException extends RuntimeException {
    public TypeCheckFailedException(String message) {
        super(message);
    }
}
