package mx.uv.lis.professionalpracticesystem.logic.customexception;

/**
 * 
 * @author andre
 */

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}