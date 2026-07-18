package ca.richasf.view.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.time.Duration;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import com.github.lgooddatepicker.components.DatePicker;

import ca.richasf.model.LoanItem;
import ca.richasf.model.LoanItemFactory;

/**
 * A modal dialog for adding a loan item.
 */
public class LoanItemAddModal extends JDialog {

    /**
     * Indicates that the user added a bad value.
     */
    private static class BadUserInputException extends Exception {

        /**
         * Constructs a new exception with a message that will be shown to the user.
         * 
         * @param message The message.
         */
        public BadUserInputException(String message) {
            super(message);
        }

    }

    /**
     * Options for the loan item type.
     * 
     * @param name          The name of the option.
     * @param selectHandler What should happen when the user selects this option.
     */
    record TypeOption(String name, Runnable selectHandler) {
        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * A creator for loan items.
     */
    @FunctionalInterface
    private static interface LoanItemCreator {
        /**
         * Creates a loan item.
         * 
         * @throws BadUserInputException If any user input is bad during creation.
         */
        LoanItem create() throws BadUserInputException;
    }

    /**
     * Helper method to throw error with efficiency.
     * 
     * @param invalid The throw condition.
     * @param message The message.
     * @throws BadUserInputException
     */
    private static void throwBadUserInputIf(boolean invalid, String message) throws BadUserInputException {
        if (invalid) {
            throw new BadUserInputException(message);
        }
    }

    private final LoanItemFactory loanItemFactory = new LoanItemFactory();

    private Consumer<LoanItem> createHandler = item -> {
    };

    private LoanItemCreator loanItemCreator = () -> null;

    private InputListView inputListView = new InputListView();

    private JComboBox<TypeOption> typeComboBox;

    private final TypeOption[] options = {
            new TypeOption("Book", () -> {
                var nameField = new JTextField();
                var publisherField = new JTextField();
                var loanedToField = new JTextField();
                var datePicker = new DatePicker();

                var spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
                var pageCountSpinner = new JSpinner(spinnerModel);

                inputListView.updateInputItems(
                        new InputListView.InputItem("Type:", typeComboBox),
                        new InputListView.InputItem("Name:", nameField),
                        new InputListView.InputItem("Due Date:", datePicker),
                        new InputListView.InputItem("Publisher:", publisherField),
                        new InputListView.InputItem("Loaned To:", loanedToField),
                        new InputListView.InputItem("# of Pages:", pageCountSpinner));

                loanItemCreator = () -> {
                    var name = nameField.getText();
                    var publisher = publisherField.getText();
                    var loanedTo = loanedToField.getText();
                    var dueDate = datePicker.getDate();

                    throwBadUserInputIf(name.isBlank(), "Name must not be blank.");
                    throwBadUserInputIf(loanedTo.isBlank(), "Loaned-to must not be blank.");
                    throwBadUserInputIf(dueDate == null, "Due date must be provided.");

                    var book = new LoanItemFactory.Book(name, publisher,
                            (Integer) pageCountSpinner.getValue());
                    var loan = new LoanItemFactory.Loan(loanedTo, dueDate);

                    return loanItemFactory.getInstance(book, loan);
                };

                pack();
            }),
            new TypeOption("Audio", () -> {
                var nameField = new JTextField();
                var publisherField = new JTextField();
                var loanedToField = new JTextField();
                var datePicker = new DatePicker();

                var hourSpinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
                var hourSpinner = new JSpinner(hourSpinnerModel);
                var minuteSpinnerModel = new SpinnerNumberModel(0, 0, 59, 1);
                var minuteSpinner = new JSpinner(minuteSpinnerModel);
                var secondSpinnerModel = new SpinnerNumberModel(0, 0, 59, 1);
                var secondSpinner = new JSpinner(secondSpinnerModel);

                inputListView.updateInputItems(
                        new InputListView.InputItem("Type:", typeComboBox),
                        new InputListView.InputItem("Name:", nameField),
                        new InputListView.InputItem("Due Date:", datePicker),
                        new InputListView.InputItem("Publisher:", publisherField),
                        new InputListView.InputItem("Loaned To:", loanedToField),
                        new InputListView.InputItem("# of Hours:", hourSpinner),
                        new InputListView.InputItem("# of Minutes:", minuteSpinner),
                        new InputListView.InputItem("# of Seconds:", secondSpinner));

                loanItemCreator = () -> {
                    var name = nameField.getText();
                    var publisher = publisherField.getText();
                    var loanedTo = loanedToField.getText();
                    var dueDate = datePicker.getDate();

                    throwBadUserInputIf(name.isBlank(), "Name must not be blank.");
                    throwBadUserInputIf(loanedTo.isBlank(), "Loaned-to must not be blank.");
                    throwBadUserInputIf(dueDate == null, "Due date must be provided.");

                    var duration = Duration.ofHours((Integer) hourSpinner.getValue())
                            .plusMinutes((Integer) minuteSpinner.getValue())
                            .plusSeconds((Integer) secondSpinner.getValue());

                    var audio = new LoanItemFactory.Audio(name, publisher, duration);
                    var loan = new LoanItemFactory.Loan(loanedToField.getText(), dueDate);

                    return loanItemFactory.getInstance(audio, loan);
                };

                pack();
            }),
            new TypeOption("Video", () -> {
                var nameField = new JTextField();
                var publisherField = new JTextField();
                var loanedToField = new JTextField();
                var datePicker = new DatePicker();

                var genreField = new JTextField();

                inputListView.updateInputItems(
                        new InputListView.InputItem("Type:", typeComboBox),
                        new InputListView.InputItem("Name:", nameField),
                        new InputListView.InputItem("Due Date:", datePicker),
                        new InputListView.InputItem("Publisher:", publisherField),
                        new InputListView.InputItem("Loaned To:", loanedToField),
                        new InputListView.InputItem("Video Genre:", genreField));

                loanItemCreator = () -> {

                    var name = nameField.getText();
                    var publisher = publisherField.getText();
                    var loanedTo = loanedToField.getText();
                    var dueDate = datePicker.getDate();
                    var genre = genreField.getText();

                    throwBadUserInputIf(name.isBlank(), "Name must not be blank.");
                    throwBadUserInputIf(loanedTo.isBlank(), "Loaned-to must not be blank.");
                    throwBadUserInputIf(dueDate == null, "Due date must be provided.");
                    throwBadUserInputIf(genre.isBlank(), "Genre must not be blank.");

                    var book = new LoanItemFactory.Video(name, publisher, genre);
                    var loan = new LoanItemFactory.Loan(loanedTo, dueDate);

                    return loanItemFactory.getInstance(book, loan);
                };

                pack();
            }),
    };

    /**
     * Constructs a new loan item add dialog.
     * 
     * @param owner The frame that owns this dialog.
     */
    public LoanItemAddModal(Frame owner) {
        super(owner, "Add a New Loan Item", true);
        typeComboBox = new JComboBox<>(options);

        setLayout(new BorderLayout());
        add(inputListView.getPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.PAGE_END);

        options[0].selectHandler().run();

        typeComboBox.addActionListener(e -> {
            options[typeComboBox.getSelectedIndex()].selectHandler().run();
        });

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Create the button panel at the bottom of the dialog.
     * 
     * @return The panel.
     */
    private JPanel createButtonPanel() {
        var panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        var createButton = new JButton("Create");
        createButton.addActionListener(e -> {
            try {
                createHandler.accept(loanItemCreator.create());
                dispose();
            } catch (BadUserInputException exception) {
                JOptionPane.showMessageDialog(LoanItemAddModal.this, exception.getMessage());
            }
        });

        var cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        panel.add(createButton);
        panel.add(cancelButton);
        return panel;
    }

    /**
     * Set the handler that handles a new loan item.
     * 
     * @param createHandler The handler.
     */
    public void setCreateHandler(Consumer<LoanItem> createHandler) {
        this.createHandler = createHandler;
    }
}
