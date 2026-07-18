package ca.richasf.loanitemstracker.view.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import ca.richasf.loanitemstracker.model.LoanItem;

/**
 * A view that lists loan items.
 */
class LoanItemsListView {

    private final JPanel panel = new JPanel();
    private Consumer<LoanItem> deleteHandler = item -> {
    };

    private final JLabel emptyMessageLabel = new JLabel("No items to show.");

    /**
     * Constructs a new {@link LoanItemsListView}.
     */
    LoanItemsListView() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    }

    private JPanel createItemPanel(int index, LoanItem item) {
        var pageStartPanel = new JPanel();
        pageStartPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        pageStartPanel.add(new JLabel("Loan Item #" + index));
        pageStartPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        var pageEndPanel = new JPanel();
        var removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> deleteHandler.accept(item));
        pageEndPanel.add(removeButton);

        var centerPanel = new LoanItemView(item).getPanel();

        var panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(pageStartPanel, BorderLayout.PAGE_START);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(pageEndPanel, BorderLayout.PAGE_END);
        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        return panel;
    }

    /**
     * Updates the loan items being displayed.
     * 
     * @param items The loan item.
     */
    void updateLoanItems(Iterable<LoanItem> items) {
        panel.removeAll();
        var index = 0;
        for (var item : items) {
            panel.add(createItemPanel(++index, item));
        }

        if (panel.getComponentCount() == 0) {
            panel.add(emptyMessageLabel);
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

    /**
     * Set the delete handler.
     * 
     * @param deleteHandler The delete handler.
     */
    void setDeleteHandler(Consumer<LoanItem> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }
}
