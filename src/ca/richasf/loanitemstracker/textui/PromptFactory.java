package ca.richasf.loanitemstracker.textui;

/**
 * Creates prompts.
 */
public class PromptFactory {
    
    /**
     * Constructs a new prompt factory.
     */
    public PromptFactory() {
	}

	/**
     * Get a prompt that asks for an integer.
     * 
     * @param parseErrorMessage The message for when an integer can't be parsed.
     * @return The prompt.
     */
    public Prompt<Integer> integer(String parseErrorMessage) {
        return new Prompt<>((raw) -> {
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                throw new PromptException(parseErrorMessage);
            }
        });
    }

    /**
     * Get a prompt that asks for a string.
     * 
     * @return The prompt.
     */
    public Prompt<String> string() {
        return new Prompt<>((v) -> v);
    }
}
