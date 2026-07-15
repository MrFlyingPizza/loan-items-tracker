package ca.richasf.view.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ca.richasf.control.LoanItemsController;
import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.LoanItemFactory;
import ca.richasf.model.VideoLoanItem;
import ca.richasf.textui.Prompt;
import ca.richasf.textui.Menu;
import static ca.richasf.textui.Validator.*;

public class LoanItemsTrackerCli {

    private final Scanner input = new Scanner(System.in);
    private final PrintStream output = System.out;
    private final Menu mainMenu = new Menu("Loan Items Tracker");;
    private final LoanItemFactory factory = new LoanItemFactory();
    private final String saveFilePath = "./list.json";
    private final LoanItemsController controller;

    /**
     * Constructs a new loan item tracker.
     */
    public LoanItemsTrackerCli() {
        controller = new LoanItemsController();

        try {
            controller.loadLoanItems();
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            System.out.printf("Failed to read save file '%s': %s", saveFilePath, e);
        }

        mainMenu.addOption("List All Items", () -> printLoans(controller.streamLoanItems()));

        mainMenu.addOption("Add an Item", () -> {

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
            controller.addLoanItem(loanItem);

            output.printf("%s has been added to the list.\n", loanItem.getName());
        });

        mainMenu.addOption("Remove an Item", () -> {
            if (controller.countLoanItems() == 0) {
                output.println("There is currently no loan to remove.");
                return;
            }

            printLoans(controller.streamLoanItems());
            var selectionErrorMessage = "Invalid selection. Enter a number between 0 and %d"
                    .formatted(controller.countLoanItems());
            var selection = Prompt.integer("Enter a valid integer.")
                    .message("Enter the item number you want to remove (0 to cancel): ")
                    .validator(bound(0, controller.countLoanItems(), selectionErrorMessage))
                    .run(input, output);

            if (selection == 0) {
                output.println("Item removal cancelled");
                return;
            }

            var index = selection - 1;
            var toRemove = controller.getLoanItem(index);
            controller.removeLoanItem(index);

            output.println("%s has been removed from the list.".formatted(toRemove.getName()));
        });

        mainMenu.addOption("List Overdue Items", () -> printLoans(controller.streamOverdueLoanItems()));

        mainMenu.addOption("List Upcoming Items", () -> printLoans(controller.streamUpcomingLoanItems()));

        mainMenu.addOption("List All Items of the Same Type", () -> {
            var typeMessage = "Enter the type of loan item to list (b: book, a: audio, v: video): ";
            var type = Prompt.string()
                    .message(typeMessage)
                    .validator(oneOf(Set.of("b", "a", "v"), "Type must be one of b/a/v."))
                    .run(input, output);

            printLoans(controller.streamSameTypeLoanItems(switch (type) {
                case "b" -> BookLoanItem.class;
                case "a" -> AudioLoanItem.class;
                case "v" -> VideoLoanItem.class;
                default -> throw new RuntimeException("Unexpected class");
            }));
        });

        mainMenu.addOption("Exit", () -> {
            output.printf("Saving the loans to %s\n", saveFilePath);

            try {
                controller.saveLoanItems();
                mainMenu.stop();
            } catch (Exception e) {
                output.printf("Failed to save the loans: %s", e);
            }

            output.println("Thank you for using our loan items tracker!");
        });
    }

    /**
     * Writes the given list of loans output.
     * 
     * @param filter Determines whether a loan item should be shown.
     */
    private void printLoans(Stream<LoanItem> loanItems) {
        var result = loanItems.collect(new LoanItemsListDisplayCollector());

        output.println(result);
    }

    /**
     * Starts the loan items tracker.
     */
    public void run() {
        mainMenu.run(input, output);
    }
}
