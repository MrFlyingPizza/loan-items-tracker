package menu;

/**
 * Indicates that the user has provided a bad input.
 */
public class BadUserInputException extends Exception {

    /**
     * Constructs this exception with a message.
     * 
     * @param message The message to indicate to the user that the input is bad.
     */
    public BadUserInputException(String message) {
        super(message);
    }

}
