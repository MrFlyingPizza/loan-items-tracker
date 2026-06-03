package menu;

import java.io.PrintStream;

public interface MenuInputAction extends MenuAction {
    void perform(PrintStream out);
}
