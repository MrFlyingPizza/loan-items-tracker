package ca.richasf;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import ca.richasf.model.LoanItem;
import ca.richasf.model.LoanItemFactory;
import ca.richasf.textui.Prompt;
import ca.richasf.textui.Menu;
import ca.richasf.textui.Validator;

/**
 * Core class for managing loans.
 */
public class LoanItemsTracker {
    private final Scanner input;
    private final PrintStream output;
    private final List<LoanItem> loans = new ArrayList<>();
    private final String filePath = "./list.json";
    private final Gson gson;
    private final Menu mainMenu;
    private final LoanItemFactory factory = new LoanItemFactory();

    private LoanItemsTracker(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        new LocalDateTypeAdapter())
                .create();

        var path = Path.of(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                load(reader);
            } catch (Exception e) {
                System.out.printf("Failed to read save file '%s': %s", filePath, e);
            }
        }

        mainMenu = new Menu("Loan Items Tracker",
                new Menu.Option("List All Items", this::handleListAllItems),
                new Menu.Option("Add an Item", this::handleAddItem),
                new Menu.Option("Remove an Item", this::handleRemoveItem),
                new Menu.Option("List Overdue Items", this::handleListOverdueItems),
                new Menu.Option("List Upcoming Items", this::handleListUpcomingItems),
                new Menu.Option("Exit", this::handleExit));
    }

    private void handleListAllItems(Scanner in, PrintStream out) {
        printLoans(loans, out);
    }

    private void handleAddItem(Scanner in, PrintStream out) {

        var name = Prompt.string()
                .message("Enter the loan item's name: ")
                .error("The loan's item name must not be blank")
                .validator(Validator.notBlank())
                .run(input, output);

        var yearDue = Prompt.integer()
                .message("Enter the year of the due date (e.g., 2026): ")
                .error("Please enter a valid year")
                .run(input, output);

        var monthDue = Prompt.integer()
                .message("Enter the month of the due date (1-12): ")
                .error("Please enter a valid month between 1 and 12")
                .validator(Validator.bound(1, 12))
                .run(input, output);

        var maxDay = YearMonth.of(yearDue, Month.of(monthDue)).lengthOfMonth();

        var dayDue = Prompt.integer()
                .message("Enter the day of the due date in the year and month (1-%d): "
                        .formatted(maxDay))
                .error("Please enter a valid month between 1 and %d".formatted(maxDay))
                .validator(Validator.bound(1, maxDay))
                .run(input, output);

        var due = LocalDate.of(yearDue, monthDue, dayDue);

        var publisher = Prompt.string()
                .message("Enter the publisher of the loan item: ")
                .run(input, output);

        var loanedTo = Prompt.string()
                .message("Enter the name to which the item is loaned: ")
                .error("The loaned-to person must not be blank")
                .validator(Validator.notBlank())
                .run(input, output);

        var type = Prompt.string()
                .message("Enter the type of loan item to add (b: book, a: audio, v: video): ")
                .error("Type must be one of b/a/v.")
                .validator(Validator.oneOf(Set.of("")))
                .run(in, out);

        var loan = new LoanItemFactory.Loan(loanedTo, due);

        var loanItem = switch (type) {
            case "b" -> {
                var pageCount = Prompt.integer()
                        .message("Enter the number of pages: ")
                        .error("The number of pages cannot be negative.")
                        .validator(Validator.nonNegative())
                        .run(input, output);
                var book = new LoanItemFactory.Book(name, publisher, pageCount);
                yield factory.getInstance(book, loan);
            }
            case "a" -> {
                var hours = Prompt.integer()
                        .message("Enter the number of hours of the audio: ")
                        .error("The number of hours cannot be negative.")
                        .validator(Validator.nonNegative())
                        .run(input, output);

                var duration = Duration.ofHours(hours);

                var minutes = Prompt.integer()
                        .message("Enter the number of minutes of the audio: ")
                        .error("The number of minutes cannot be negative.")
                        .validator(Validator.nonNegative())
                        .run(input, output);

                duration.plusMinutes(minutes);

                var seconds = Prompt.integer()
                        .message("Enter the number of seconds of the audio: ")
                        .error("The number of seconds cannot be negative.")
                        .validator(Validator.nonNegative())
                        .run(input, output);

                duration.plusSeconds(seconds);

                var audio = new LoanItemFactory.Audio(name, publisher, duration);
                yield factory.getInstance(audio, loan);
            }
            case "v" -> {

                var genre = Prompt.string()
                        .message("Enter the genre (if unknown type \"unknown\"): ")
                        .error("The genre must not be blank.")
                        .validator(Validator.notBlank())
                        .run(input, output);

                var video = new LoanItemFactory.Video(name, publisher, genre);
                yield factory.getInstance(video, loan);
            }
            default -> throw new RuntimeException("Unexpected type.");
        };

        loans.add(loanItem);

        out.printf("%s has been added to the list.\n", loanItem.getName());
    }

    private void handleRemoveItem(Scanner in, PrintStream out) {
        if (loans.size() == 0) {
            out.println("There is currently no loan to remove.");
            return;
        }

        printLoans(loans, out);
        var selection = Prompt.integer()
                .message("Enter the item number you want to remove (0 to cancel): ")
                .error("Invalid selection. Enter a number between 0 and %d"
                        .formatted(loans.size()))
                .validator(Validator.bound(0, loans.size()))
                .run(in, out);

        if (selection == 0) {
            out.println("Item removal cancelled");
            return;
        }

        var index = selection - 1;
        var toRemove = loans.get(index);
        loans.remove(index);

        out.println("%s has been removed from the list.".formatted(toRemove.getName()));
    }

    private void handleListOverdueItems(Scanner in, PrintStream out) {
        printLoans(loans.stream()
                .filter(loan -> loan.getDue().isBefore(LocalDate.now()))
                .toList(),
                out);
    }

    private void handleListUpcomingItems(Scanner in, PrintStream out) {
        printLoans(
                loans.stream()
                        .filter(loan -> !loan.getDue().isBefore(LocalDate.now()))
                        .toList(),
                out);
    }

    private void handleExit(Scanner in, PrintStream out) {
        out.printf("Saving the loans to %s\n", filePath);
        try (var writer = Files.newBufferedWriter(Path.of(filePath))) {
            save(writer);

            mainMenu.stop();

            out.println("Thank you for using our loan items tracker!");
        } catch (IOException e) {
            out.printf("Failed to save the loans: %s", e);
        }
    }

    /**
     * Read loans from a source.
     * 
     * @param reader The source.
     * @throws JsonIOException     If JSON reading error occurs.
     * @throws JsonSyntaxException If JSON is malformed.
     */
    private void load(Reader reader) throws JsonIOException, JsonSyntaxException {
        loans.addAll(gson.fromJson(reader,
                TypeToken.getParameterized(List.class, LoanItem.class)
                        .getType()));
    }

    /**
     * Write loans to a destination.
     * 
     * @param writer The destination.
     * @throws IOException If writing loans fails.
     */
    private void save(Writer writer) throws IOException {
        writer.write(gson.toJson(loans));
    }

    /**
     * Writes the given list of loans out.
     * 
     * @param loans The loans to write.
     * @param out   The output to write to.
     */
    private void printLoans(List<LoanItem> loans, PrintStream out) {
        var builder = new StringBuilder();
        if (loans.isEmpty()) {
            builder.append("No items to show");
        } else {
            loans.stream().forEach(new Consumer<LoanItem>() {
                private int counter = 1;

                @Override
                public void accept(LoanItem loan) {
                    builder.append('#').append(counter++).append('\n').append(loan).append('\n');
                }
            });
        }
        out.println(builder);
    }

    public void run() {
        mainMenu.run(input, output);
    }

    /**
     * Entrypoint.
     * 
     * @param args CLI arguments.
     * @throws Exception If an error occurs.
     */
    public static void main(String[] args) throws Exception {
        new LoanItemsTracker(new Scanner(System.in), System.out).run();
    }
}
