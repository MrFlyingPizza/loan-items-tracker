package ca.richasf.textui;

import java.util.Set;

public class ValidatorFactory {

    /**
     * Get a validator that does nothing.
     * 
     * @param <T> The type this validator is for.
     * @return A validator that does nothing.
     */
    public <T> Validator<T> pass() {
        return (value) -> {
        };
    }

    /**
     * Get a validator that ensures an int is within a range.
     * 
     * @param min          The minimum int inclusive.
     * @param max          The maximum int inclusive.
     * @param errorMessage The message to show on failure.
     * @return The validator.
     */
    public Validator<Integer> bound(int min, int max, String errorMessage) {
        return (value) -> {
            if (value < min || value > max) {
                throw new PromptException(errorMessage);
            }
        };
    }

    /**
     * Get a validator that ensures a string is not blank.
     * 
     * @param errorMessage The message to show on failure.
     * @return The validator.
     */
    public Validator<String> notBlank(String errorMessage) {
        return (value) -> {
            if (value.isBlank()) {
                throw new PromptException(errorMessage);
            }
        };
    }

    /**
     * Get a validator that ensure a value is in a set.
     * 
     * @param <T>          The type of the value.
     * @param items        The set of valid items.
     * @param errorMessage The message to show on failure.
     * @return The validator.
     */
    public <T> Validator<T> oneOf(Set<T> items, String errorMessage) {
        return (value) -> {
            if (!items.contains(value)) {
                throw new PromptException(errorMessage);
            }
        };
    }

    /**
     * Get a validator that ensure a value is not negative.
     * 
     * @param errorMessage The message to show on failure.
     * @return The validator.
     */
    public Validator<Integer> nonNegative(String errorMessage) {
        return (value) -> {
            if (value < 0) {
                throw new PromptException(errorMessage);
            }
        };
    }
}
