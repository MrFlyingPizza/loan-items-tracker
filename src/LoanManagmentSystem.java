import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
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
import menu.SelectionMenu;

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
        MAIN_MENU.addOption("List All Items", LoanManagmentSystem::handleListAllItems);
        MAIN_MENU.addOption("Add an Item", null);
        MAIN_MENU.addOption("Remove an Item", null);
        MAIN_MENU.addOption("List Overdue Items", LoanManagmentSystem::handleListOverdueItems);
        MAIN_MENU.addOption("List Upcoming Items", LoanManagmentSystem::handleListUpcomingItems);
        MAIN_MENU.addOption("Exit", LoanManagmentSystem::handleExit);
    }

    private static void load(Reader reader) {
        LOANS.addAll(GSON.fromJson(reader, TypeToken.getParameterized(List.class, Loan.class).getType()));
    }

    private static void save(Writer writer) throws IOException {
        writer.write(GSON.toJson(LOANS));
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

    private static void printLoans(List<Loan> loans, PrintStream out) {
        var builder = new StringBuilder();
        if (loans.isEmpty()) {
            builder.append("No items to show.");
        } else {
            loans.stream().forEach(new StringBuilderLoanAppender(builder));
        }
        out.println(builder);
    }

    private static void handleListAllItems(Scanner in, PrintStream out) {
        printLoans(LOANS, out);
    }

    private static void handleListOverdueItems(Scanner in, PrintStream out) {
        printLoans(LOANS.stream().filter(loan -> loan.getDue().isBefore(LocalDate.now())).toList(), out);
    }

    private static void handleListUpcomingItems(Scanner in, PrintStream out) {
        printLoans(LOANS.stream().filter(loan -> !loan.getDue().isBefore(LocalDate.now())).toList(), out);
    }

    public static void main(String[] args) throws Exception {
        MAIN_MENU.run(new Scanner(System.in), System.out);
    }
}
