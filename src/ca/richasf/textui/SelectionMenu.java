package ca.richasf.textui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A menu that allows the user to perform actions using through selections.
 */
public class SelectionMenu {

    private static record Option(String name, MenuAction action) {
    }

    private final String title;

    private final List<Option> options = new ArrayList<>();

    private boolean running = true;

    /**
     * Creates a new menu.
     * 
     * @param title The title of the menu.
     */
    public SelectionMenu(String title) {
        this.title = title;
    }

    /**
     * Stop the menu from cycling.
     */
    public void stop() {
        running = false;
    }

    /**
     * Add an option for the user to select from.
     * 
     * @param name   The name of the option shown to the user.
     * @param action An action that takes inputs but produce no output.
     */
    public void addOption(String name, MenuAction action) {
        options.add(new Option(name, action));
    }

    /**
     * Start the display->prompt->dispatch cycle.
     * 
     * @param in  The input to read from.
     * @param out The output to write to.
     */
    public void run(Scanner in, PrintStream out) {
        if (options.size() <= 0) {
            throw new RuntimeException("Cannot run menu when there is no option");
        }

        while (running) {
            display(out);
            var value = prompt().run(in, out);
            dispatch(value, in, out);
        }
    }

    /**
     * Write the menu to the output.
     * 
     * @param out The output.
     */
    void display(PrintStream out) {
        /* Start banner */
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

        /* End banner */
        /* Start current date */
        builder.append("Today is: ")
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy MMMM dd")))
                .append('\n');
        /* End current date */

        /* Start options */
        for (var i = 0; i < options.size(); i++) {
            builder.append(i + 1);
            builder.append(": ");
            builder.append(options.get(i).name);
            builder.append('\n');
        }
        /* End options */

        out.print(builder);
    }

    /**
     * Prompts the user for a selection.
     */
    Prompt<Integer> prompt() {
        return Prompt.integer()
                .message("Choose an option by entering 1-%d: "
                        .formatted(options.size()))
                .error("Invalid selection. Enter a number between 1 and %d"
                        .formatted(options.size()))
                .validator(Validator.boundedInt(1, options.size()));
    }

    /**
     * Dispatch the user's selection.
     */
    void dispatch(Integer selection, Scanner in, PrintStream out) {
        options.get(selection - 1).action().perform(in, out);
    }
}