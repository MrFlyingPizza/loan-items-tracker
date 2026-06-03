package menu;

import java.io.PrintStream;
import java.util.Scanner;

public interface Prompt<T> {

    /**
     * Tell the user what you want.
     * 
     * @return
     */
    void prompt(PrintStream out);

    /**
     * Read a value.
     * 
     * @param in The input.
     * @return The value read.
     * @throws BadUserInputException Indicate that the user input was bad.
     */
    T read(Scanner in) throws BadUserInputException;

    /**
     * Execute the prompt by asking the user and reading until a satisfactory value
     * is read.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     * @return
     */
    default T execute(Scanner in, PrintStream out) {
        boolean shouldRead = true;
        T value = null;
        while (shouldRead) {
            prompt(out);
            try {
                value = read(in);
                shouldRead = false;
            } catch (BadUserInputException e) {
                out.println(e.getMessage());
            }
        }
        return value;
    }
}
