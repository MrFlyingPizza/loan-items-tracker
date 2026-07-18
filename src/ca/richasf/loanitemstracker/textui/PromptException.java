package ca.richasf.loanitemstracker.textui;

/**
 * Indicates that the user has provided a bad input.
 */
public class PromptException extends Exception {

    /**
     * Creates an new exception.
     * 
     * @param message A message that tells the user what failed.
     */
    public PromptException(String message) {
        super(message);
    }
}
