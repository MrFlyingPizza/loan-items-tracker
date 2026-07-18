package ca.richasf.loanitemstracker.view.gui;

import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Display a list of options and handles their selection.
 * 
 * @param <T> The type of the value.
 */
class ListOptionsView<T> {

    /**
     * Represents an option.
     * 
     * @param name  The name of the option to display.
     * @param value The value to dispatch when selected.
     */
    static record Option<T>(String name, T value) {
    }

    private final JPanel panel = new JPanel();

    private Consumer<T> selectHandler;

    /**
     * Constructs a new {@link ListOptionsView}.
     * 
     * @param defaultOption The option that is selected by default.
     * @param options       The remainder of the options.
     */
    @SafeVarargs
    ListOptionsView(Option<T> defaultOption, Option<T>... options) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        var buttonGroup = new ButtonGroup();
        var defaultButton = new JToggleButton(defaultOption.name(), true);
        buttonGroup.add(defaultButton);
        panel.add(defaultButton);
        defaultButton.addActionListener(e -> selectHandler.accept(defaultOption.value()));
        for (var option : options) {
            var button = new JToggleButton(option.name());
            buttonGroup.add(button);
            panel.add(button);
            button.addActionListener(e -> selectHandler.accept(option.value()));
        }
    }

    /**
     * Set the handler for when a value is selected.
     * @param selectHandler The handler.
     */
    void setSelectHandler(Consumer<T> selectHandler) {
        this.selectHandler = selectHandler;
    }

    /**
     * Get the panel to this view.
     * @return The panel.
     */
    JPanel getPanel() {
        return panel;
    }
}
