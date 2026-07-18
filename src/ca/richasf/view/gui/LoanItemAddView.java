package ca.richasf.view.gui;

import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DateFormatter;
import ca.richasf.model.LoanItem;
import ca.richasf.model.LoanItemFactory;

public class LoanItemAddView {
    record TypeOption(String name, Runnable selectHandler) {
        @Override
        public String toString() {
            return name;
        }
    }

    private final LoanItemFactory loanItemFactory = new LoanItemFactory();

    private final JPanel panel = new JPanel();

    private Consumer<LoanItem> createHandler = item -> {
    };

    private Runnable cancelHandler = () -> {
    };

    private Supplier<LoanItem> loanItemCreator = () -> null;

    private InputListView inputListView = new InputListView();

    private JComboBox<TypeOption> typeComboBox;

    private final TypeOption[] options = {
            new TypeOption("Book", () -> {
                var dateFormatter = new DateFormatter(new SimpleDateFormat("yyyy/MM/dd"));
                dateFormatter.setOverwriteMode(true);
                var dueField = new JFormattedTextField(dateFormatter);

                var nameField = new JTextField();
                var publisherField = new JTextField();
                var loanedToField = new JTextField();

                var spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
                var pageCountSpinner = new JSpinner(spinnerModel);

                inputListView.updateInputItems(
                        new InputListView.InputItem("Type:", typeComboBox),
                        new InputListView.InputItem("Name:", nameField),
                        new InputListView.InputItem("Due Date:", dueField),
                        new InputListView.InputItem("Publisher:", publisherField),
                        new InputListView.InputItem("Loaned To:", loanedToField),
                        new InputListView.InputItem("# of Pages:", pageCountSpinner));

                loanItemCreator = () -> {
                    var book = new LoanItemFactory.Book(
                            nameField.getText(),
                            publisherField.getText(),
                            (Integer) pageCountSpinner.getValue());
                    var loan = new LoanItemFactory.Loan(
                            loanedToField.getText(),
                            LocalDate.parse(dueField.getText()));

                    return loanItemFactory.getInstance(book, loan);
                };
            }),
            new TypeOption("Audio", () -> {
                var dateFormatter = new DateFormatter(new SimpleDateFormat("yyyy/MM/dd"));
                dateFormatter.setAllowsInvalid(false);
                dateFormatter.setOverwriteMode(true);
                var dueField = new JFormattedTextField(dateFormatter);

                var nameField = new JTextField();
                var publisherField = new JTextField();
                var loanedToField = new JTextField();

                var hourSpinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
                var hourSpinner = new JSpinner(hourSpinnerModel);
                var minuteSpinnerModel = new SpinnerNumberModel(0, 0, 59, 1);
                var minuteSpinner = new JSpinner(minuteSpinnerModel);
                var secondSpinnerModel = new SpinnerNumberModel(0, 0, 59, 1);
                var secondSpinner = new JSpinner(secondSpinnerModel);

                inputListView.updateInputItems(
                        new InputListView.InputItem("Type:", typeComboBox),
                        new InputListView.InputItem("Name:", nameField),
                        new InputListView.InputItem("Due Date:", dueField),
                        new InputListView.InputItem("Publisher:", publisherField),
                        new InputListView.InputItem("Loaned To:", loanedToField),
                        new InputListView.InputItem("# of Hours:", hourSpinner),
                        new InputListView.InputItem("# of Minutes:", minuteSpinner),
                        new InputListView.InputItem("# of Seconds:", secondSpinner));

                loanItemCreator = () -> {
                    var duration = Duration.ofHours((Integer) hourSpinner.getValue())
                            .plusMinutes((Integer) minuteSpinner.getValue())
                            .plusSeconds((Integer) secondSpinner.getValue());

                    var audio = new LoanItemFactory.Audio(
                            nameField.getText(),
                            publisherField.getText(),
                            duration);
                    var loan = new LoanItemFactory.Loan(
                            loanedToField.getText(),
                            LocalDate.parse(dueField.getText()));

                    return loanItemFactory.getInstance(audio, loan);
                };
            }),
            new TypeOption("Video", () -> {
                var dateFormatter = new DateFormatter(new SimpleDateFormat("yyyy/MM/dd"));
                dateFormatter.setAllowsInvalid(false);
                dateFormatter.setOverwriteMode(true);
                var dueField = new JFormattedTextField(dateFormatter);

                var nameField = new JTextField();
                var publisherField = new JTextField();
                var loanedToField = new JTextField();

                var genreField = new JTextField();

                inputListView.updateInputItems(
                        new InputListView.InputItem("Type:", typeComboBox),
                        new InputListView.InputItem("Name:", nameField),
                        new InputListView.InputItem("Due Date:", dueField),
                        new InputListView.InputItem("Publisher:", publisherField),
                        new InputListView.InputItem("Loaned To:", loanedToField),
                        new InputListView.InputItem("Video Genre:", genreField));

                loanItemCreator = () -> {
                    var book = new LoanItemFactory.Book(
                            nameField.getText(),
                            publisherField.getText(),
                            Integer.parseInt(genreField.getText()));
                    var loan = new LoanItemFactory.Loan(
                            loanedToField.getText(),
                            LocalDate.parse(dueField.getText()));

                    return loanItemFactory.getInstance(book, loan);
                };
            }),
    };

    public LoanItemAddView() {
        typeComboBox = new JComboBox<>(options);

        options[0].selectHandler().run();

        typeComboBox.addActionListener(e -> {
            options[typeComboBox.getSelectedIndex()].selectHandler().run();
        });

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(inputListView.getPanel());
        panel.add(createButtonPanel());
    }

    private JPanel createButtonPanel() {
        var panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        var createButton = new JButton("Create");
        createButton.addActionListener(e -> createHandler.accept(loanItemCreator.get()));

        var cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> cancelHandler.run());

        panel.add(createButton);
        panel.add(cancelButton);
        return panel;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setCreateHandler(Consumer<LoanItem> createHandler) {
        this.createHandler = createHandler;
    }

    public void setCancelHandler(Runnable cancelHandler) {
        this.cancelHandler = cancelHandler;
    }
}
