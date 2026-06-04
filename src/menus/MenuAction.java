package menus;

import java.io.PrintStream;
import java.util.Scanner;

@FunctionalInterface
public interface MenuAction {
    void perform(Scanner in, PrintStream out);
}
