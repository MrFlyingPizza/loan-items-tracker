package menu;

import java.io.PrintStream;
import java.util.Scanner;

public interface Menu<T> extends Prompt<T> {
    /**
     * Tell what the user to do.
     */
    void display(PrintStream out);

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

    /**
     * Start the display->prompt->read->dispatch cycle.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     */
    default void run(Scanner in, PrintStream out) {
        while (isRunning()) {
            display(out);
            var value = execute(in, out);
            dispatch(value, in, out);
        }
    }
}
