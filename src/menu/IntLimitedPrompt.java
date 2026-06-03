package menu;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Prompts for an integer.
 */
public class IntLimitedPrompt implements Prompt<Integer> {
    private final int min, max;
    private final String errorMessage;
    private final IntPrompt prompt;

    /**
     * 
     * Construct a new integer prompt.
     * 
     * @param minInclusive  The minimum integer allowed.
     * @param maxInclusive  The maximum integer allowed.
     * @param promptMessage Message to show the user to request input.
     * @param errorMessage  Message to show when user's input is bad.
     */
    public IntLimitedPrompt(int minInclusive, int maxInclusive, String promptMessage, String errorMessage) {
        this.min = minInclusive;
        this.max = maxInclusive;
        this.errorMessage = errorMessage;
        this.prompt = new IntPrompt(promptMessage, errorMessage);
    }

    @Override
    public void request(PrintStream out) {
        prompt.request(out);
    }

    @Override
    public Integer receive(Scanner in) throws BadUserInputException {
        var value = prompt.receive(in);

        if (value < min || value > max) {
            throw new BadUserInputException(errorMessage);
        }

        return value;
    }

}
