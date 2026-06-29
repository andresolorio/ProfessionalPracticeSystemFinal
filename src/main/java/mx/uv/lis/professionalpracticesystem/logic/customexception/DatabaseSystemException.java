package mx.uv.lis.professionalpracticesystem.logic.customexception;

/**
 * 
 * @author andre
 */

public class DatabaseSystemException extends Exception {
    public DatabaseSystemException(String message) {
        super(message);
    }

    public DatabaseSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}