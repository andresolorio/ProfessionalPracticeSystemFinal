package mx.uv.lis.professionalpracticesystem.logic.customexception;

/**
 *
 * @author andre
 */

public class DataIntegrityException extends DatabaseSystemException {
    public DataIntegrityException(String message) {
        super(message);
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
