package mx.uv.lis.professionalpracticesystem.logic.customexception;

/**
 *
 * @author andre
 */

public class EntityNotFoundException extends DatabaseSystemException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
