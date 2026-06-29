package mx.uv.lis.professionalpracticesystem.logic.customexception;

/**
 *
 * @author andre
 */

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
