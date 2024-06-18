package exception;
// наследование от RuntimeException и сами передаем тект исключения

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }
}
