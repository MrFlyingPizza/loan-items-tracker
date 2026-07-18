package ca.richasf.view.cli;

import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Scanner;
import java.util.Set;
import ca.richasf.control.LoanItemPredicateFactory;
import ca.richasf.control.LoanItemsController;
import ca.richasf.control.PersistenceException;
import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItemFactory;
import ca.richasf.model.VideoLoanItem;
import ca.richasf.textui.PromptFactory;
import ca.richasf.textui.ValidatorFactory;
import ca.richasf.textui.Menu;

/**
 * The command line loan items tracker.
 */
public class LoanItemsTrackerCli {

    private final ValidatorFactory validators = new ValidatorFactory();
    private final PromptFactory prompts = new PromptFactory();
    private final LoanItemPredicateFactory predicates = new LoanItemPredicateFactory();
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
        } catch (PersistenceException e) {
            System.out.printf("Failed to read save file '%s': %s", saveFilePath, e);
        }

        mainMenu.addOption("List All Items", () -> controller.streamLoanItems()
                .collect(new LoanItemsPrintCollector(output)));

        mainMenu.addOption("Add an Item", () -> {

            var name = prompts.string()
                    .message("Enter the loan item's name: ")
                    .validator(validators.notBlank("The name must not be blank."))
                    .run(input, output);

            var yearDue = prompts.integer("Enter a valid integer.")
                    .message("Enter the year of the due date (e.g., 2026): ")
                    .run(input, output);

            var monthDue = prompts.integer("Enter a valid integer.")
                    .message("Enter the month of the due date (1-12): ")
                    .validator(validators.bound(1, 12,
                            "Please enter a valid month between 1 and 12."))
                    .run(input, output);

            var maxDay = YearMonth.of(yearDue, Month.of(monthDue)).lengthOfMonth();

            var dayDue = prompts.integer("Enter a valid integer.")
                    .message("Enter the day of the due date in the year and month (1-%d): "
                            .formatted(maxDay))
                    .validator(validators.bound(1, maxDay,
                            "Please enter a valid month between 1 and %d".formatted(maxDay)))
                    .run(input, output);

            var due = LocalDate.of(yearDue, monthDue, dayDue);

            var publisher = prompts.string()
                    .message("Enter the publisher of the loan item: ")
                    .run(input, output);

            var loanedTo = prompts.string()
                    .message("Enter the name to which the item is loaned: ")
                    .validator(validators.notBlank("The loaned-to name must not be blank."))
                    .run(input, output);

            var typeMessage = "Enter the type of loan item to add (b: book, a: audio, v: video): ";
            var type = prompts.string()
                    .message(typeMessage)
                    .validator(validators.oneOf(Set.of("b", "a", "v"),
                            "Type must be one of b/a/v."))
                    .run(input, output);

            var loan = new LoanItemFactory.Loan(loanedTo, due);

            var loanItem = switch (type) {
                case "b" -> {
                    var pageCountErrorMessage = "The number of pages cannot be negative.";
                    var pageCount = prompts.integer("Enter a valid integer.")
                            .message("Enter the number of pages: ")
                            .validator(validators.nonNegative(pageCountErrorMessage))
                            .run(input, output);
                    var book = new LoanItemFactory.Book(name, publisher, pageCount);
                    yield factory.getInstance(book, loan);
                }
                case "a" -> {
                    var hoursErrorMessage = "The number of hours cannot be negative.";
                    var hours = prompts.integer("Enter a valid integer.")
                            .message("Enter the number of hours of the audio: ")
                            .validator(validators.nonNegative(hoursErrorMessage))
                            .run(input, output);

                    var duration = Duration.ofHours(hours);

                    var minutesErrorMessage = "The number of minutes cannot be negative.";
                    var minutes = prompts.integer("Enter a valid integer.")
                            .message("Enter the number of minutes of the audio: ")
                            .validator(validators.nonNegative(minutesErrorMessage))
                            .run(input, output);

                    duration = duration.plusMinutes(minutes);

                    var secondsErrorMessage = "The number of seconds cannot be negative.";
                    var seconds = prompts.integer("Enter a valid integer.")
                            .message("Enter the number of seconds of the audio: ")
                            .validator(validators.nonNegative(secondsErrorMessage))
                            .run(input, output);

                    duration = duration.plusSeconds(seconds);

                    var audio = new LoanItemFactory.Audio(name, publisher, duration);
                    yield factory.getInstance(audio, loan);
                }
                case "v" -> {
                    var genre = prompts.string()
                            .message("Enter the genre (if unknown type \"unknown\"): ")
                            .validator(validators.notBlank("The genre must not be blank."))
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

            var loanItems = controller.streamLoanItems().toList();

            loanItems.stream().collect(new LoanItemsPrintCollector(output));
            var selectionErrorMessage = "Invalid selection. Enter a number between 0 and %d"
                    .formatted(controller.countLoanItems());
            var selection = prompts.integer("Enter a valid integer.")
                    .message("Enter the item number you want to remove (0 to cancel): ")
                    .validator(validators.bound(0,
                            controller.countLoanItems(),
                            selectionErrorMessage))
                    .run(input, output);

            if (selection == 0) {
                output.println("Item removal cancelled");
                return;
            }

            var index = selection - 1;
            var toRemove = loanItems.get(index);
            controller.removeLoanItem(toRemove);

            output.println(toRemove.getName() + " has been removed from the list.");
        });

        mainMenu.addOption("List Overdue Items", () -> controller.streamLoanItems()
                .filter(predicates.overdue())
                .collect(new LoanItemsPrintCollector(output)));

        mainMenu.addOption("List Upcoming Items", () -> controller.streamLoanItems()
                .filter(predicates.upcoming())
                .collect(new LoanItemsPrintCollector(output)));

        mainMenu.addOption("List All Items of the Same Type", () -> {
            var typeMessage = "Enter the type of loan item to list (b: book, a: audio, v: video): ";
            var type = prompts.string()
                    .message(typeMessage)
                    .validator(validators.oneOf(Set.of("b", "a", "v"),
                            "Type must be one of b/a/v."))
                    .run(input, output);

            var typeClass = switch (type) {
                case "b" -> BookLoanItem.class;
                case "a" -> AudioLoanItem.class;
                case "v" -> VideoLoanItem.class;
                default -> throw new RuntimeException("Unexpected class");
            };

            controller.streamLoanItems()
                    .filter(predicates.sameType(typeClass))
                    .collect(new LoanItemsPrintCollector(output));
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
     * Starts the loan items tracker.
     */
    public void start() {
        mainMenu.run(input, output);
    }
}
