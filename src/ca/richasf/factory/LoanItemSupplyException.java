package ca.richasf.factory;

/**
 * Indicates a problem while supplying a loan item.
 */
public class LoanItemSupplyException extends Exception {

    /**
     * Constructs a new exception with a message.
     * @param message The message to shown the user.
     */
    public LoanItemSupplyException(String message) {
        super(message);
    }
}
