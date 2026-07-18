package ca.richasf.loanitemstracker.textui;

/**
 * Validates a value that the user gave.
 * 
 * @param <T> The type of the value.
 */
public interface Validator<T> {

    /**
     * Validates the user value.
     * 
     * @param value The value.
     * @throws PromptException If validation failed.
     */
    void validate(T value) throws PromptException;
}
