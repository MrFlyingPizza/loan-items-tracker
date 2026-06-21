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
import java.util.function.Predicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.LoanItemFactory;
import ca.richasf.model.VideoLoanItem;
import ca.richasf.textui.Prompt;
import ca.richasf.textui.Menu;
import static ca.richasf.textui.Validator.*;

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
                new Menu.Option("List All Items", this::handleListAll),
                new Menu.Option("Add an Item", this::handleAdd),
                new Menu.Option("Remove an Item", this::handleRemove),
                new Menu.Option("List Overdue Items", this::handleListOverdue),
                new Menu.Option("List Upcoming Items", this::handleListUpcoming),
                new Menu.Option("List All Items of the Same Type", this::handleListSameType),
                new Menu.Option("Exit", this::handleExit));
    }

    private void handleListAll(Scanner in, PrintStream out) {
        printLoans((item) -> true);
    }

    private void handleAdd(Scanner in, PrintStream out) {

        var name = Prompt.string()
                .message("Enter the loan item's name: ")
                .validator(notBlank("The name must not be blank."))
                .run(input, output);

        var yearDue = Prompt.integer("Enter a valid integer.")
                .message("Enter the year of the due date (e.g., 2026): ")
                .run(input, output);

        var monthDue = Prompt.integer("Enter a valid integer.")
                .message("Enter the month of the due date (1-12): ")
                .validator(bound(1, 12,
                        "Please enter a valid month between 1 and 12."))
                .run(input, output);

        var maxDay = YearMonth.of(yearDue, Month.of(monthDue)).lengthOfMonth();

        var dayDue = Prompt.integer("Enter a valid integer.")
                .message("Enter the day of the due date in the year and month (1-%d): "
                        .formatted(maxDay))
                .validator(bound(1, maxDay,
                        "Please enter a valid month between 1 and %d".formatted(maxDay)))
                .run(input, output);

        var due = LocalDate.of(yearDue, monthDue, dayDue);

        var publisher = Prompt.string()
                .message("Enter the publisher of the loan item: ")
                .run(input, output);

        var loanedTo = Prompt.string()
                .message("Enter the name to which the item is loaned: ")
                .validator(notBlank("The loaned-to name must not be blank."))
                .run(input, output);

        var typeMessage = "Enter the type of loan item to add (b: book, a: audio, v: video): ";
        var type = Prompt.string()
                .message(typeMessage)
                .validator(oneOf(Set.of("b", "a", "v"), "Type must be one of b/a/v."))
                .run(in, out);

        var loan = new LoanItemFactory.Loan(loanedTo, due);

        var loanItem = switch (type) {
            case "b" -> {
                var pageCountErrorMessage = "The number of pages cannot be negative.";
                var pageCount = Prompt.integer("Enter a valid integer.")
                        .message("Enter the number of pages: ")
                        .validator(nonNegative(pageCountErrorMessage))
                        .run(input, output);
                var book = new LoanItemFactory.Book(name, publisher, pageCount);
                yield factory.getInstance(book, loan);
            }
            case "a" -> {
                var hoursErrorMessage = "The number of hours cannot be negative.";
                var hours = Prompt.integer("Enter a valid integer.")
                        .message("Enter the number of hours of the audio: ")
                        .validator(nonNegative(hoursErrorMessage))
                        .run(input, output);

                var duration = Duration.ofHours(hours);

                var minutesErrorMessage = "The number of minutes cannot be negative.";
                var minutes = Prompt.integer("Enter a valid integer.")
                        .message("Enter the number of minutes of the audio: ")
                        .validator(nonNegative(minutesErrorMessage))
                        .run(input, output);

                duration.plusMinutes(minutes);

                var secondsErrorMessage = "The number of seconds cannot be negative.";
                var seconds = Prompt.integer("Enter a valid integer.")
                        .message("Enter the number of seconds of the audio: ")
                        .validator(nonNegative(secondsErrorMessage))
                        .run(input, output);

                duration.plusSeconds(seconds);

                var audio = new LoanItemFactory.Audio(name, publisher, duration);
                yield factory.getInstance(audio, loan);
            }
            case "v" -> {
                var genre = Prompt.string()
                        .message("Enter the genre (if unknown type \"unknown\"): ")
                        .validator(notBlank("The genre must not be blank."))
                        .run(input, output);

                var video = new LoanItemFactory.Video(name, publisher, genre);
                yield factory.getInstance(video, loan);
            }
            default -> throw new RuntimeException("Unexpected type.");
        };

        loans.add(loanItem);

        out.printf("%s has been added to the list.\n", loanItem.getName());
    }

    private void handleRemove(Scanner in, PrintStream out) {
        if (loans.size() == 0) {
            out.println("There is currently no loan to remove.");
            return;
        }

        printLoans((item) -> true);
        var selectionErrorMessage = "Invalid selection. Enter a number between 0 and %d"
                .formatted(loans.size());
        var selection = Prompt.integer("Enter a valid integer.")
                .message("Enter the item number you want to remove (0 to cancel): ")
                .validator(bound(0, loans.size(), selectionErrorMessage))
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

    private void handleListOverdue(Scanner in, PrintStream out) {
        printLoans(loan -> loan.getDue().isBefore(LocalDate.now()));
    }

    private void handleListUpcoming(Scanner in, PrintStream out) {
        printLoans(loan -> !loan.getDue().isBefore(LocalDate.now()));
    }

    private void handleListSameType(Scanner in, PrintStream out) {
        var typeMessage = "Enter the type of loan item to list (b: book, a: audio, v: video): ";
        var type = Prompt.string()
                .message(typeMessage)
                .validator(oneOf(Set.of("b", "a", "v"), "Type must be one of b/a/v."))
                .run(in, out);

        printLoans(loan -> loan.getClass().equals(switch (type) {
            case "b" -> BookLoanItem.class;
            case "a" -> AudioLoanItem.class;
            case "v" -> VideoLoanItem.class;
            default -> throw new RuntimeException("Unexpected class");
        }));
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
     * @param filter Determines whether a loan item should be shown.
     */
    private void printLoans(Predicate<LoanItem> filter) {
        var builder = new StringBuilder();
        if (loans.isEmpty()) {
            builder.append("No items to show");
        } else {
            loans.stream().filter(filter).forEach(new Consumer<LoanItem>() {
                private int counter = 1;

                @Override
                public void accept(LoanItem loan) {
                    builder.append("Loan Item Type: ").append(switch (loan) {
                        case BookLoanItem item -> "Book";
                        case AudioLoanItem item -> "Audio";
                        case VideoLoanItem item -> "Video";
                        default -> "Unknown";
                    });
                    builder.append('#').append(counter++).append('\n').append(loan).append('\n');
                }
            });
        }
        output.println(builder);
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
