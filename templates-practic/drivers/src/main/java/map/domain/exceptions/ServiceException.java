package map.domain.exceptions;

public class ServiceException extends RuntimeException {
    public ServiceException() {
    }

    public ServiceException(String message) {
      super("Service throwed: " + message);
    }

    public ServiceException(String message, Throwable cause) {
      super("Service throwed: " + message, cause);
    }

    public ServiceException(Throwable cause) {
      super(cause);
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super("Service throwed: " + message, cause, enableSuppression, writableStackTrace);
    }
}
