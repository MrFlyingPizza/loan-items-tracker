package ca.richasf;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import ca.richasf.loans.Loan;
import ca.richasf.menus.SelectionMenu;
import ca.richasf.prompts.Prompt;
import ca.richasf.prompts.Validator;

/**
 * Core class for managing loans.
 */
public class LoanItemsTracker {

    /**
     * Unused.
     */
    private LoanItemsTracker() {
    }

    private static final List<Loan> LOANS = new ArrayList<>();
    private static final String SAVE_FILE_NAME = "./list.json";
    private static final File SAVE_FILE = new File(SAVE_FILE_NAME);
    private static final Gson GSON;
    private static final SelectionMenu MAIN_MENU;

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        new LocalDateTypeAdapter())
                .create();

        if (SAVE_FILE.exists()) {
            try (var reader = new FileReader(SAVE_FILE)) {
                load(reader);
            } catch (Exception e) {
                System.out.printf("Failed to read save file '%s': %s", SAVE_FILE_NAME, e);
            }
        }

        MAIN_MENU = new SelectionMenu("Loan Items Tracker");

        MAIN_MENU.addOption("List All Items", (in, out) -> printLoans(LOANS, out));

        MAIN_MENU.addOption("Add an Item", (in, out) -> {

            var name = Prompt.string()
                    .message("Enter the loan item's name: ")
                    .error("The loan's item name must not be blank")
                    .validator(Validator.notBlank())
                    .run(in, out);

            var yearDue = Prompt.integer()
                    .message("Enter the year of the due date (e.g., 2026): ")
                    .error("Please enter a valid year")
                    .run(in, out);

            var monthDue = Prompt.integer()
                    .message("Enter the month of the due date (1-12): ")
                    .error("Please enter a valid month between 1 and 12")
                    .validator(Validator.boundedInt(1, 12))
                    .run(in, out);

            var maxDay = YearMonth.of(yearDue, Month.of(monthDue)).lengthOfMonth();

            var dayDue = Prompt.integer()
                    .message("Enter the day of the due date in the year and month (1-%d): "
                            .formatted(maxDay))
                    .error("Please enter a valid month between 1 and %d".formatted(maxDay))
                    .validator(Validator.boundedInt(1, maxDay))
                    .run(in, out);

            var publisher = Prompt.string()
                    .message("Enter the publisher of the loan item: ")
                    .run(in, out);

            var loanedTo = Prompt.string()
                    .message("Enter the name to which the item is loaned: ")
                    .error("The loaned-to person must not be blank")
                    .validator(Validator.notBlank())
                    .run(in, out);

            var due = LocalDate.of(yearDue, monthDue, dayDue);

            var loan = new Loan(name, publisher, loanedTo, due);

            LOANS.add(loan);

            out.printf("%s has been added to the list.\n", name);
        });

        MAIN_MENU.addOption("Remove an Item", (in, out) -> {
            if (LOANS.size() == 0) {
                out.println("There is currently no loan to remove.");
                return;
            }

            printLoans(LOANS, out);
            var selection = Prompt.integer()
                    .message("Enter the item number you want to remove (0 to cancel): ")
                    .error("Invalid selection. Enter a number between 0 and %d"
                            .formatted(LOANS.size()))
                    .validator(Validator.boundedInt(0, LOANS.size()))
                    .run(in, out);

            if (selection == 0) {
                out.println("Item removal cancelled");
                return;
            }

            var index = selection - 1;
            var toRemove = LOANS.get(index);
            LOANS.remove(index);

            out.println("%s has been removed from the list.".formatted(toRemove.getName()));
        });

        MAIN_MENU.addOption("List Overdue Items", (in, out) -> printLoans(LOANS.stream()
                .filter(loan -> loan.getDue().isBefore(LocalDate.now()))
                .toList(),
                out));

        MAIN_MENU.addOption("List Upcoming Items", (in, out) -> printLoans(
                LOANS.stream()
                        .filter(loan -> !loan.getDue().isBefore(LocalDate.now()))
                        .toList(),
                out));

        MAIN_MENU.addOption("Exit", (in, out) -> {
            out.printf("Saving the loans to %s\n", SAVE_FILE);

            try (var writer = new FileWriter(SAVE_FILE)) {
                save(writer);

                MAIN_MENU.stop();

                out.println("Thank you for using our loan items tracker!");
            } catch (IOException e) {
                out.printf("Failed to save the loans: %s", e);
            }
        });
    }

    /**
     * Read loans from a source.
     * 
     * @param reader The source.
     * @throws JsonIOException     If JSON reading error occurs.
     * @throws JsonSyntaxException If JSON is malformed.
     */
    private static void load(Reader reader) throws JsonIOException, JsonSyntaxException {
        LOANS.addAll(GSON.fromJson(reader,
                TypeToken.getParameterized(List.class, Loan.class)
                        .getType()));
    }

    /**
     * Write loans to a destination.
     * 
     * @param writer The destination.
     * @throws IOException If writing loans fails.
     */
    private static void save(Writer writer) throws IOException {
        writer.write(GSON.toJson(LOANS));
    }

    /**
     * Writes the given list of loans out.
     * 
     * @param loans The loans to write.
     * @param out   The output to write to.
     */
    private static void printLoans(List<Loan> loans, PrintStream out) {
        var builder = new StringBuilder();
        if (loans.isEmpty()) {
            builder.append("No items to show");
        } else {
            loans.stream().forEach(new Consumer<Loan>() {
                private int counter = 1;

                @Override
                public void accept(Loan loan) {
                    builder.append('#').append(counter++).append('\n').append(loan).append('\n');
                }
            });
        }
        out.println(builder);
    }

    /**
     * Entrypoint.
     * 
     * @param args CLI arguments.
     * @throws Exception If an error occurs.
     */
    public static void main(String[] args) throws Exception {
        MAIN_MENU.run(new Scanner(System.in), System.out);
    }
}
