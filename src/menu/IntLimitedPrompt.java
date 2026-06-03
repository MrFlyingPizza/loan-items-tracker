package menu;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Prompts for an integer.
 */
public class IntLimitedPrompt implements Prompt<Integer> {
    private final int min;
    private final int max;
    private final String requestMessage, parseErrorMessage, outOfBoundMessage;

    /**
     * 
     * Construct a new integer prompt.
     * 
     * @param minInclusive      The minimum integer allowed.
     * @param maxInclusive      The maximum integer allowed.
     * @param requestMessage    Message to show the user to request input.
     * @param parseErrorMessage Message to show when an int cannot be parsed.
     * @param outOfBoundMessage Message to show when the user's integer input is
     *                          outside the limits.
     */
    public IntLimitedPrompt(
            int minInclusive,
            int maxInclusive,
            String requestMessage,
            String parseErrorMessage,
            String outOfBoundMessage) {
        this.min = minInclusive;
        this.max = maxInclusive;
        this.requestMessage = requestMessage;
        this.parseErrorMessage = parseErrorMessage;
        this.outOfBoundMessage = outOfBoundMessage;
    }

    @Override
    public void request(PrintStream out) {
        out.printf(requestMessage, min, max);
    }

    @Override
    public Integer receive(Scanner in) throws BadUserInputException {
        var input = in.nextLine();

        var value = -1;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new BadUserInputException(parseErrorMessage.formatted(min, max));
        }

        if (value < min || value > max) {
            throw new BadUserInputException(outOfBoundMessage.formatted(min, max));
        }

        return value;
    }

}
