package launcher.exceptions;

public class WrongVideoException extends RuntimeException {
    public WrongVideoException(String message) {
        super(message);
    }

    public WrongVideoException(String message, Throwable cause) {
        super(message, cause);
    }
}
