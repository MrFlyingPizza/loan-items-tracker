package menu;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private static record Option(String name, MenuAction action) {
    }

    private final String title;

    private final List<Option> options = new ArrayList<>();

    /**
     * Creates a new menu.
     * 
     * @param title The title of the menu.
     */
    public Menu(String title) {
        this.title = title;
    }

    /**
     * Add an option for the user to select from.
     * 
     * @param name   The name of the option shown to the user.
     * @param action An action that takes inputs but produce no output.
     */
    public void addInputOption(String name, MenuInputAction action) {
        options.add(new Option(name, action));
    }

    /**
     * Add an option for the user to select from.
     * 
     * @param name   The name of the option shown to the user.
     * @param action An action that takes no input but produces outputs.
     */
    public void addOutputOption(String name, MenuOutputAction action) {
        options.add(new Option(name, action));
    }

    /**
     * Add an option for the user to select from.
     * 
     * @param name   The name of the option shown to the user.
     * @param action An action that can both take input and produce output.
     */
    public void addOption(String name, MenuInputOutputAction action) {
        options.add(new Option(name, action));
    }

    /**
     * Write the menu to the output.
     * 
     * @param out The output.
     */
    public void display(PrintStream out) {
        final var C = "#";
        final var S = ' ';
        var builder = new StringBuilder();
        for (var i = 0; i < title.length() + 4; i++) {
            builder.append('#');
        }

        builder.append('\n');

        builder.append(C);
        builder.append(S);
        builder.append(title);
        builder.append(S);
        builder.append(C);

        builder.append('\n');

        for (var i = 0; i < title.length() + 4; i++) {
            builder.append('#');
        }

        builder.append('\n');

        for (var i = 0; i < options.size(); i++) {
            builder.append(i + 1);
            builder.append(": ");
            builder.append(options.get(i).name);
            builder.append('\n');
        }

        out.println(builder.toString());
    }

    /**
     * Read a user's choice from the input.
     * 
     * @param in The input.
     */
    public void prompt(Scanner in) throws BadUserInputException {
        var input = in.nextLine();

    }
}
