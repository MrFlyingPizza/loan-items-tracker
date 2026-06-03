package menu;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SelectionMenu implements Menu<Integer> {

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
     * Add an option for the user to select from.
     * 
     * @param name   The name of the option shown to the user.
     * @param action An action that takes inputs but produce no output.
     */
    public void addOption(String name, MenuAction action) {
        options.add(new Option(name, action));
    }

    /**
     * Write the menu to the output.
     * 
     * @param out The output.
     */
    @Override
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

        out.print(builder);
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void run(Scanner in, PrintStream out) {
        if (options.size() <= 0) {
            throw new RuntimeException("Cannot run menu when there is no option");
        }

        Menu.super.run(in, out);
    }

    @Override
    public Prompt<Integer> prompt() {
        var promptMessage = "Choose an option by entering 1-%d: ".formatted(options.size());
        var errorMessage = "Invalid selection. Enter a number between 1 and %d".formatted(options.size());
        return new IntLimitedPrompt(1, options.size(), promptMessage, errorMessage);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void dispatch(Integer selection, Scanner in, PrintStream out) {
        options.get(selection - 1).action().perform(in, out);
    }
}