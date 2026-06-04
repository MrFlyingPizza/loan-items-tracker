package prompts;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Defines the behaviour of a prompt.
 * 
 * @param <T> What kind of value this prompt is for.
 */
public interface Prompt<T> {

    /**
     * Tell the user what you want.
     * 
     * @param out Output to write to.
     */
    void request(PrintStream out);

    /**
     * Read a value.
     * 
     * @param in The input.
     * @return The value read.
     * @throws BadUserInputException Indicate that the user input was bad.
     */
    T receive(Scanner in) throws BadUserInputException;

    /**
     * Execute the prompt by asking the user and reading until a satisfactory value
     * is read.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     * @return The value provided by the user.
     */
    default T execute(Scanner in, PrintStream out) {
        boolean shouldRead = true;
        T value = null;
        while (shouldRead) {
            request(out);
            try {
                value = receive(in);
                shouldRead = false;
            } catch (BadUserInputException e) {
                out.println(e.getMessage());
            }
        }
        return value;
    }
}
