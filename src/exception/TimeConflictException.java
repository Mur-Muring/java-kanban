package exception;
// Исключение для конфликта по времени задач
public class TimeConflictException extends RuntimeException {
    public TimeConflictException(String message) {
        super(message);
    }
}
