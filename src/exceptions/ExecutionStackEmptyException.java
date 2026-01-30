package exceptions;

public class ExecutionStackEmptyException extends RuntimeException {
    public ExecutionStackEmptyException() {
        super();
    }

    public ExecutionStackEmptyException(String message) {
        super(message);
    }

    public ExecutionStackEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionStackEmptyException(Throwable cause) {
        super(cause);
    }
}
