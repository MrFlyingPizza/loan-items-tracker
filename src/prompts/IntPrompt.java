package prompts;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * A prompt for an integer.
 */
public class IntPrompt implements Prompt<Integer> {
    private final String requestMessage;
    private final String parseErrorMessage;

    /**
     * Creates a new prompt for a integer.
     * 
     * @param requestMessage    The message to display to the user when prompting.
     * @param parseErrorMessage The message to display when the user gives bad
     *                          input that causes a parse error.
     */
    public IntPrompt(String requestMessage, String parseErrorMessage) {
        this.requestMessage = requestMessage;
        this.parseErrorMessage = parseErrorMessage;
    }

    @Override
    public void request(PrintStream out) {
        out.print(requestMessage);
    }

    @Override
    public Integer receive(Scanner in) throws BadUserInputException {
        var input = in.nextLine();

        var value = -1;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new BadUserInputException(parseErrorMessage);
        }

        return value;
    }

}
