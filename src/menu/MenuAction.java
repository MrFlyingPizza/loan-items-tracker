package menu;

import java.io.PrintStream;
import java.util.Scanner;

public interface MenuAction {
    void perform(Scanner in, PrintStream out);
}
