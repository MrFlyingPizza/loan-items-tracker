package menu;

import java.io.PrintStream;
import java.util.Scanner;

public interface Menu<T> {
    /**
     * Tell what the user to do.
     */
    void display(PrintStream out);

    /**
     * Prompt for a value.
     * 
     * @return
     */
    void prompt(PrintStream out);

    /**
     * Read the value.
     * 
     * @param in The input.
     * @return The value read.
     * @throws BadUserInputException Indicate that the user input was bad.
     */
    T read(Scanner in) throws BadUserInputException;

    /**
     * Dispatch the given value.
     * 
     * @param value The value passed by the user.
     */
    void dispatch(T value, Scanner in, PrintStream out);

    /**
     * Determines if the event loop should keep going.
     * 
     * @return Whether to continue running.
     */
    boolean isRunning();

    /**
     * Stop the running cycle.
     */
    void stop();

    private T promptReadCycle(Scanner in, PrintStream out) {
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

    /**
     * Start the display->prompt->read->dispatch cycle.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     */
    default void run(Scanner in, PrintStream out) {
        while (isRunning()) {
            display(out);
            var value = promptReadCycle(in, out);
            dispatch(value, in, out);
        }
    }
}
