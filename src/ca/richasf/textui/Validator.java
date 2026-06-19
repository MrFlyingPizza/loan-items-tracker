package ca.richasf.textui;

import java.util.Set;

/**
 * Validates a value that the user gave.
 * 
 * @param <T> The type of the value.
 */
public interface Validator<T> {

    /**
     * Get a validator that does nothing.
     * 
     * @param <T> The type this validator is for.
     * @return A validator that does nothing.
     */
    static <T> Validator<T> pass() {
        return (value) -> {
        };
    }

    /**
     * Get a validator that ensures an int is within a range.
     * 
     * @param min The minimum int inclusive.
     * @param max The maximum int inclusive.
     * @return The validator.
     */
    static Validator<Integer> boundedInt(int min, int max) {
        return (value) -> {
            if (value < min || value > max) {
                throw new ValidateException();
            }
        };
    }

    /**
     * Get a validator that ensures a string is not blank.
     * 
     * @return The validator.
     */
    static Validator<String> notBlank() {
        return (value) -> {
            if (value.isBlank()) {
                throw new ValidateException();
            }
        };
    }

    /**
     * Get a validator that ensure a value is in a set.
     * @param <T> The type of the value.
     * @param items The set of valid items.
     * @return The validator.
     */
    static <T> Validator<T> oneOf(Set<T> items) {
        return (value) -> {
            if (!items.contains(value)) {
                throw new ValidateException();
            }
        };
    }

    /**
     * Validates the user value.
     * 
     * @param value The value.
     * @throws ValidateException If validation failed.
     */
    void validate(T value) throws ValidateException;
}
