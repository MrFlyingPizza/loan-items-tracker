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
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import loan.Loan;
import menu.IntLimitedPrompt;
import menu.IntPrompt;
import menu.SelectionMenu;
import menu.StringPrompt;

public class LoanManagmentSystem {

    private static class StringBuilderLoanAppender implements Consumer<Loan> {
        private int counter = 1;

        private final StringBuilder builder;

        StringBuilderLoanAppender(StringBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void accept(Loan loan) {
            builder.append('#').append(counter++).append('\n').append(loan).append('\n');
        }

    }

    private static final List<Loan> LOANS = new ArrayList<>();
    private static final String SAVE_FILE_NAME = "loans.json";
    private static final File SAVE_FILE = new File(SAVE_FILE_NAME);
    private static final Gson GSON;
    private static final SelectionMenu MAIN_MENU;

    static {
        GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
            @Override
            public void write(JsonWriter out, LocalDate value) throws IOException {
                out.value(value.toString());
            }

            @Override
            public LocalDate read(JsonReader in) throws IOException {
                return LocalDate.parse(in.nextString());
            }
        }).create();

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

            var name = new StringPrompt("Enter the loan item's name: ").execute(in, out);

            var yearDue = new IntPrompt("Enter the year of the due date (e.g., 2026): ", "Please enter a valid year.")
                    .execute(in, out);

            var monthDue = new IntLimitedPrompt(
                    Month.JANUARY.ordinal(),
                    Month.DECEMBER.ordinal(),
                    "Enter the month of the due date (1-12): ",
                    "Please enter a valid month between 1 and 12.").execute(in, out);

            var maxMonth = YearMonth.of(yearDue, Month.of(monthDue)).lengthOfMonth();
            var dayDue = new IntLimitedPrompt(
                    Month.JANUARY.ordinal(),
                    maxMonth,
                    "Enter the day of the due date in the year and month (1-%d): ".formatted(maxMonth),
                    "Please enter a valid month between 1 and %d.".formatted(maxMonth)).execute(in, out);

            var publisher = new StringPrompt("Enter the publisher of the loan item: ").execute(in, out);

            var loanedTo = new StringPrompt("Enter the name to which the item is loaned: ").execute(in, out);

            var due = LocalDate.of(yearDue, monthDue, dayDue);

            var loan = new Loan(name, publisher, loanedTo, due);

            LOANS.add(loan);

            out.printf("%s has been added to the list.\n", name);
        });

        MAIN_MENU.addOption("Remove an Item", (in, out) -> {
            printLoans(LOANS, out);
            var selection = new IntLimitedPrompt(0, LOANS.size(),
                    "Enter the item number you want to remove (0 to cancel): ",
                    "Invalid selection. Enter a number between 0 and %d".formatted(LOANS.size())).execute(in, out);
            if (selection == 0) {
                out.println("Item removal cancelled.");
                return;
            }

            LOANS.remove(selection - 1);
        });

        MAIN_MENU.addOption("List Overdue Items",
                (in, out) -> printLoans(LOANS.stream().filter(loan -> loan.getDue().isBefore(LocalDate.now())).toList(),
                        out));

        MAIN_MENU.addOption("List Upcoming Items", (in, out) -> printLoans(
                LOANS.stream().filter(loan -> !loan.getDue().isBefore(LocalDate.now())).toList(), out));

        MAIN_MENU.addOption("Exit", LoanManagmentSystem::handleExit);
    }

    private static void load(Reader reader) {
        LOANS.addAll(GSON.fromJson(reader, TypeToken.getParameterized(List.class, Loan.class).getType()));
    }

    private static void save(Writer writer) throws IOException {
        writer.write(GSON.toJson(LOANS));
    }

    private static void printLoans(List<Loan> loans, PrintStream out) {
        var builder = new StringBuilder();
        if (loans.isEmpty()) {
            builder.append("No items to show.");
        } else {
            loans.stream().forEach(new StringBuilderLoanAppender(builder));
        }
        out.println(builder);
    }

    private static void handleExit(Scanner in, PrintStream out) {
        out.printf("Saving the loans to %s\n", SAVE_FILE);

        try (var writer = new FileWriter(SAVE_FILE)) {
            save(writer);

            MAIN_MENU.stop();

            out.println("Thank you for using our loan items tracker!");
        } catch (IOException e) {
            out.printf("Failed to save the loans: %s", e);
        }
    }

    public static void main(String[] args) throws Exception {
        MAIN_MENU.run(new Scanner(System.in), System.out);
    }
}
