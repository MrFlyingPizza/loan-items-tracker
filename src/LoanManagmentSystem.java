import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import loan.Loan;
import menu.Menu;

public class LoanManagmentSystem {
    private static final List<Loan> LOANS = new ArrayList<>();
    private static final String SAVE_FILE_NAME = "loans.json";
    private static final File SAVE_FILE = new File(SAVE_FILE_NAME);
    private static final Gson GSON;
    private static final Menu MAIN_MENU;
    private static boolean RUNNING = true;

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
                LOANS.addAll(GSON.fromJson(reader, TypeToken.getParameterized(List.class, Loan.class).getType()));
            } catch (Exception e) {
                System.out.printf("Failed to read save file '%s': %s", SAVE_FILE_NAME, e);
            }
        }

        MAIN_MENU = new Menu("Loan Items Tracker");
        MAIN_MENU.addOutputOption("List All Items", (PrintStream out) -> {

        });
        MAIN_MENU.addInputOption("Add an Item", null);
        MAIN_MENU.addInputOption("Remove an Item", null);
        MAIN_MENU.addInputOption("List Overdue Items", null);
        MAIN_MENU.addInputOption("List Upcoming Items", null);
        MAIN_MENU.addInputOption("Exit", (_1, _2) -> RUNNING = false);
    }

    private static void runMainLoop() {
        var scanner = new Scanner(System.in);
        while (RUNNING) {
            MAIN_MENU.display(System.out);
            MAIN_MENU.handleChoice(scanner);
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
