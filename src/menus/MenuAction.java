package menus;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * What a menu does.
 */
@FunctionalInterface
public interface MenuAction {
    /**
     * Performs this action.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     */
    void perform(Scanner in, PrintStream out);
}
