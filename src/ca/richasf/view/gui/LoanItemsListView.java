package ca.richasf.view.gui;

import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import ca.richasf.model.LoanItem;

class LoanItemsListView {

    private final JPanel panel = new JPanel();
    private Consumer<LoanItem> deleteHandler = item -> {};

    LoanItemsListView() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    }

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

    JPanel getPanel() {
        return panel;
    }

    void setDeleteHandler(Consumer<LoanItem> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }
}
