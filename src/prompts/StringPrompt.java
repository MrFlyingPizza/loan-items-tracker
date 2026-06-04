package prompts;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * A prompt for a string.
 */
public class StringPrompt implements Prompt<String> {
    private final String promptMessage;

    /**
     * Creates a new prompt for a string.
     * 
     * @param requestMessage The message to display to the user to ask for an input.
     */
    public StringPrompt(String requestMessage) {
        this.promptMessage = requestMessage;
    }

    @Override
    public void request(PrintStream out) {
        out.print(promptMessage);
    }

    @Override
    public String receive(Scanner in) throws BadUserInputException {
        return in.nextLine();
    }

}
