package exceptions;

public class UndefinedAddressException extends RuntimeException {
    public UndefinedAddressException(String message) {
        super(message);
    }
}
