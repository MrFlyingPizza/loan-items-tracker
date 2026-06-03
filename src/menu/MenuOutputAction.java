package menu;

import java.io.PrintStream;

public interface MenuOutputAction extends MenuAction {
    void perform(PrintStream out);
}
