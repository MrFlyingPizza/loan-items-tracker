package ca.richasf.view.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ca.richasf.gson.extras.RuntimeTypeAdapterFactory;
import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.LoanItemFactory;
import ca.richasf.model.VideoLoanItem;
import ca.richasf.textui.Prompt;
import ca.richasf.textui.Menu;
import static ca.richasf.textui.Validator.*;

public class LoanItemsTrackerCli {

    private static final Type type = TypeToken.getParameterized(
            List.class,
            LoanItem.class)
            .getType();

    private final Scanner input = new Scanner(System.in);
    private final PrintStream output = System.out;
    private final List<LoanItem> loans = new ArrayList<>();
    private final String filePath = "./list.json";
    private final Gson gson;
    private final Menu mainMenu = new Menu("Loan Items Tracker");;
    private final LoanItemFactory factory = new LoanItemFactory();

    /**
     * Constructs a new loan item tracker.
     */
    public LoanItemsTrackerCli() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(LoanItem.class)
                        .registerSubtype(BookLoanItem.class, "Book")
                        .registerSubtype(AudioLoanItem.class, "Audio")
                        .registerSubtype(VideoLoanItem.class, "Video"))
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                    @Override
                    public void write(JsonWriter out, LocalDate value) throws IOException {
                        out.value(value.toString());
                    }

                    @Override
                    public LocalDate read(JsonReader in) throws IOException {
                        return LocalDate.parse(in.nextString());
                    }
                })
                .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {
                    @Override
                    public void write(JsonWriter out, Duration value) throws IOException {
                        out.value(value.toMillis());
                    }

                    @Override
                    public Duration read(JsonReader in) throws IOException {
                        return Duration.ofMillis(in.nextLong());
                    }
                })
                .create();

        var path = Path.of(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                load(reader);
            } catch (Exception e) {
                System.out.printf("Failed to read save file '%s': %s", filePath, e);
            }
        }

        mainMenu.addOption("List All Items", this::handleListAllLoanItems);
        mainMenu.addOption("Add an Item", this::handleAddLoanItems);
        mainMenu.addOption("Remove an Item", this::handleRemoveLoanItems);
        mainMenu.addOption("List Overdue Items", this::handleListOverdueLoanItems);
        mainMenu.addOption("List Upcoming Items", this::handleListUpcomingLoanItems);
        mainMenu.addOption("List All Items of the Same Type", this::handleListSameTypeLoanItems);
        mainMenu.addOption("Exit", this::handleExit);
    }

    protected void handleListAllLoanItems() {
        printLoans((item) -> true);
    }

    protected void handleAddLoanItems() {

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
                .run(input, output);

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

                duration = duration.plusMinutes(minutes);

                var secondsErrorMessage = "The number of seconds cannot be negative.";
                var seconds = Prompt.integer("Enter a valid integer.")
                        .message("Enter the number of seconds of the audio: ")
                        .validator(nonNegative(secondsErrorMessage))
                        .run(input, output);

                duration = duration.plusSeconds(seconds);

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

        output.printf("%s has been added to the list.\n", loanItem.getName());

    }

    protected void handleRemoveLoanItems() {

        if (loans.size() == 0) {
            output.println("There is currently no loan to remove.");
            return;
        }

        printLoans((item) -> true);
        var selectionErrorMessage = "Invalid selection. Enter a number between 0 and %d"
                .formatted(loans.size());
        var selection = Prompt.integer("Enter a valid integer.")
                .message("Enter the item number you want to remove (0 to cancel): ")
                .validator(bound(0, loans.size(), selectionErrorMessage))
                .run(input, output);

        if (selection == 0) {
            output.println("Item removal cancelled");
            return;
        }

        var index = selection - 1;
        var toRemove = loans.get(index);
        loans.remove(index);

        output.println("%s has been removed from the list.".formatted(toRemove.getName()));

    }

    protected void handleListOverdueLoanItems() {
        printLoans(loan -> loan.getDue().isBefore(LocalDate.now()));

    }

    protected void handleListUpcomingLoanItems() {
        printLoans(loan -> !loan.getDue().isBefore(LocalDate.now()));

    }

    protected void handleListSameTypeLoanItems() {

        var typeMessage = "Enter the type of loan item to list (b: book, a: audio, v: video): ";
        var type = Prompt.string()
                .message(typeMessage)
                .validator(oneOf(Set.of("b", "a", "v"), "Type must be one of b/a/v."))
                .run(input, output);

        printLoans(loan -> loan.getClass().equals(switch (type) {
            case "b" -> BookLoanItem.class;
            case "a" -> AudioLoanItem.class;
            case "v" -> VideoLoanItem.class;
            default -> throw new RuntimeException("Unexpected class");
        }));
    }

    protected void handleExit() {

        output.printf("Saving the loans to %s\n", filePath);
        try (var writer = Files.newBufferedWriter(Path.of(filePath))) {
            save(writer);

            mainMenu.stop();

            output.println("Thank you for using our loan items tracker!");
        } catch (IOException e) {
            output.printf("Failed to save the loans: %s", e);
        }
    }

    /**
     * Convert a loan item to be displayed as a string.
     * 
     * @param loanItem The loan item to display.
     * @return The display string.
     */
    private String formatLoanItem(LoanItem loanItem) {
        var result = new StringBuilder();

        var type = switch (loanItem) {
            case BookLoanItem item -> "Book";
            case AudioLoanItem item -> "Audio";
            case VideoLoanItem item -> "Video";
            default -> "Unknown";
        };

        result.append("Loan Item Type: ").append(type).append('\n');
        result.append(loanItem.getName()).append('\n');
        result.append("- published by ").append(loanItem.getPublisher()).append('\n');
        result.append("- loaned to ").append(loanItem.getLoanedTo()).append('\n');

        var due = loanItem.getDue();
        result.append("- ");
        result.append("due on ").append(due);
        var daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), due);
        result.append(" (");
        if (daysUntilDue > 0) {
            result.append("due in ")
                    .append(daysUntilDue)
                    .append(" days(s)");
        } else if (daysUntilDue < 0) {
            result.append("overdue by ")
                    .append(-daysUntilDue)
                    .append(" days(s)");
        } else {
            result.append("due today");
        }
        result.append(')').append('\n');

        result.append("- ");
        switch (loanItem) {
            case BookLoanItem book -> result.append(book.getPageCount()).append(" pages");
            case AudioLoanItem audio -> {
                var duration = audio.getDuration();
                result.append(duration.toHours()).append(" hour(s) ")
                        .append(duration.toMinutesPart()).append(" minute(s) ")
                        .append(duration.toSecondsPart()).append(" second(s) ")
                        .append("long");
            }
            case VideoLoanItem video -> result.append(video.getGenre()).append(" genre");
            default -> {
            }
        }
        result.append('\n');

        return result.toString();
    }

    /**
     * Read loans from a source.
     * 
     * @param reader The source.
     * @throws JsonIOException     If JSON reading error occurs.
     * @throws JsonSyntaxException If JSON is malformed.
     */
    private void load(Reader reader) throws JsonIOException, JsonSyntaxException {
        loans.addAll(gson.fromJson(reader, type));
    }

    /**
     * Write loans to a destination.
     * 
     * @param writer The destination.
     * @throws IOException If writing loans fails.
     */
    private void save(Writer writer) throws IOException {
        writer.write(gson.toJson(loans, type));
    }

    /**
     * Writes the given list of loans output.
     * 
     * @param filter Determines whether a loan item should be shown.
     */
    private void printLoans(Predicate<LoanItem> filter) {
        var builder = new StringBuilder();
        var count = loans.stream().filter(filter).map(new Function<LoanItem, LoanItem>() {
            private int counter = 1;

            @Override
            public LoanItem apply(LoanItem item) {
                builder.append('#').append(counter++).append('\n')
                        .append(formatLoanItem(item));
                return item;
            }
        }).count();

        if (count == 0) {
            builder.append("No items to show.\n");
        }

        output.println(builder);
    }

    /**
     * Starts the loan items tracker.
     */
    public void run() {
        mainMenu.run(input, output);
    }
}
