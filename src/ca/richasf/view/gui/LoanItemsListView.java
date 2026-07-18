package ca.richasf.view.gui;

import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import ca.richasf.model.LoanItem;

/**
 * A view that lists loan items.
 */
class LoanItemsListView {

    private final JPanel panel = new JPanel();
    private Consumer<LoanItem> deleteHandler = item -> {
    };

    /**
     * Constructs a new {@link LoanItemsListView}.
     */
    LoanItemsListView() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    }

    /**
     * Updates the loan items being displayed.
     * 
     * @param items The loan item.
     */
    void updateLoanItems(Iterable<LoanItem> items) {
        panel.removeAll();
        for (var item : items) {
            var loanItemDisplay = new LoanItemView(item);
            var itemPanel = loanItemDisplay.getPanel();
            itemPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            loanItemDisplay.setDeleteHandler(toDelete -> {
                panel.remove(itemPanel);
                panel.revalidate();
                panel.repaint();
                deleteHandler.accept(toDelete);
            });

            panel.add(itemPanel);
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
