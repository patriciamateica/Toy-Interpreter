package exceptions;

public class NotAHeapAddressException extends RuntimeException {
    public NotAHeapAddressException(String message) {
        super(message);
    }
}
