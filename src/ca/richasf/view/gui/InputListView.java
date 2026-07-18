package ca.richasf.view.gui;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Manages a panel of inputs.
 */
class InputListView {
    record InputItem(String label, Component input) {
    };

    private final JPanel panel = new JPanel();

    /**
     * Creates a new {@link InputListView}.
     */
    InputListView() {
    }

    /**
     * Update the inputs.
     * 
     * @param inputItems The input items.
     */
    void updateInputItems(InputItem... inputItems) {
        panel.removeAll();

        var layout = new GroupLayout(panel);

        panel.setLayout(layout);

        var labelGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        var inputGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        var rowGroup = layout.createSequentialGroup();

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(labelGroup)
                .addGroup(inputGroup));

        layout.setVerticalGroup(rowGroup);

        for (var inputItem : inputItems) {
            var label = new JLabel(inputItem.label());
            var input = inputItem.input();

            labelGroup.addComponent(label);
            inputGroup.addComponent(inputItem.input());
            rowGroup.addGroup(layout.createParallelGroup()
                    .addComponent(label, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                    .addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE));
        }

        panel.revalidate();
        panel.repaint();
    }

    /**
     * Get the panel to this view.
     * 
     * @return The panel.
     */
    JPanel getPanel() {
        return panel;
    }
}
