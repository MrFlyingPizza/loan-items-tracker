package menus;

import java.io.PrintStream;
import java.util.Scanner;

import prompts.Prompt;

/**
 * The behaviours of a menu.
 * 
 * @param <T> What this menu dispatches as a value.
 */
public interface Menu<T> {
    /**
     * Tell what the user to do.
     * 
     * @param out The output to write to.
     */
    void display(PrintStream out);

    /**
     * Get a prompt for a value to prompt the user with.
     * 
     * @return A prompt.
     */
    Prompt<T> prompt();

    /**
     * Dispatch the given value.
     * 
     * @param value The value passed by the user.
     * @param in    The input to read from.
     * @param out   The output to write to.
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

    /**
     * Start the display->prompt->dispatch cycle.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     */
    default void run(Scanner in, PrintStream out) {
        while (isRunning()) {
            display(out);
            var value = prompt().execute(in, out);
            dispatch(value, in, out);
        }
    }
}
