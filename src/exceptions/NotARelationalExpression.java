package exceptions;

public class NotARelationalExpression extends RuntimeException {
    public NotARelationalExpression(String message) {
        super(message);
    }
}
