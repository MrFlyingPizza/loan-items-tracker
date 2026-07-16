package ca.richasf.view.gui;

import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

class TypeOptionsView<T> {
    static record Option<T>(String name, T value) {
    }

    @SafeVarargs
    static <T> TypeOptionsView<T> of(Option<T> defaultOption, Option<T>... options) {
        return new TypeOptionsView<>(defaultOption, options);
    }

    static <T> Option<T> option(String name, T value) {
        return new Option<>(name, value);
    }

    private final JPanel panel = new JPanel();

    private Consumer<T> selectHandler;

    @SafeVarargs
    public TypeOptionsView(Option<T> defaultOption, Option<T>... options) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        var buttonGroup = new ButtonGroup();
        var defaultButton = new JRadioButton(defaultOption.name(), true);
        buttonGroup.add(defaultButton);
        panel.add(defaultButton);
        defaultButton.addActionListener(e -> selectHandler.accept(defaultOption.value()));
        for (var option : options) {
            var button = new JRadioButton(option.name());
            buttonGroup.add(button);
            panel.add(button);
            button.addActionListener(e -> selectHandler.accept(option.value()));
        }
    }

    public void setSelectHandler(Consumer<T> selectHandler) {
        this.selectHandler = selectHandler;
    }

    public JPanel getPanel() {
        return panel;
    }
}
