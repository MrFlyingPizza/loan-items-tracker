package menu;

import java.io.PrintStream;
import java.util.Scanner;

public interface MenuInputOutputAction extends MenuAction {
    void perform(Scanner in, PrintStream out);
}
