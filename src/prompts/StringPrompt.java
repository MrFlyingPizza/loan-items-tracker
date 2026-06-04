package prompts;

import java.io.PrintStream;
import java.util.Scanner;

public class StringPrompt implements Prompt<String> {
    private final String promptMessage;

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
