package map.domain.exceptions;

public class RepositoryException extends RuntimeException {
    public RepositoryException() {
    }

    public RepositoryException(String message) {
        super("Repository throwed: " + message);
    }

    public RepositoryException(String message, Throwable cause) {
        super("Repository throwed: " + message, cause);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }

    public RepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("Repository throwed: " + message, cause, enableSuppression, writableStackTrace);
    }

}
