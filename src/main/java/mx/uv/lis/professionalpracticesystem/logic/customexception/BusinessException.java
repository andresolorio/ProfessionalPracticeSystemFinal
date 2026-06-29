package mx.uv.lis.professionalpracticesystem.logic.customexception;

/**
 * 
 * @author andre
 */

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
