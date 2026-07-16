package ca.richasf.control;

/**
 * Indicates persistence failure.
 */
public class PersistenceException extends Exception {

    /**
     * Creates a new exception.
     * @param cause The cause for this exception.
     */
    PersistenceException(Throwable cause) {
        super(cause);
    }
    
}
