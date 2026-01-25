package map.domain.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    public ValidationException(String message) {
        super("Validator throwed: " + message);
    }

    public ValidationException(String message, Throwable cause) {
        super("Validator throwed: " + message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("Validator throwed: " + message, cause, enableSuppression, writableStackTrace);
    }
}
