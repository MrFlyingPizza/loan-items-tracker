package ca.richasf.textui;

/**
 * Parses a string into a value.
 * 
 * @param <T> The type of value that this parser parses into.
 */
public interface Parser<T> {
    /**
     * Parses a value from the string.
     * 
     * @param raw The raw string.
     * @return The value.
     * @throws PromptException If parsing failed.
     */
    T parse(String raw) throws PromptException;
}
