package exception;

import java.io.IOError;

public class ManagerSaveException extends IOError {

    public ManagerSaveException(Throwable message) {
        super(message);
    }
}
