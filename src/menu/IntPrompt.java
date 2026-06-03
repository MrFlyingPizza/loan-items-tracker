package menu;

import java.io.PrintStream;
import java.util.Scanner;

public class IntPrompt implements Prompt<Integer> {
    private final String requestMessage;
    private final String parseErrorMessage;

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
