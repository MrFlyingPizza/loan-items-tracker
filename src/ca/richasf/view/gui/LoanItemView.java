package ca.richasf.view.gui;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.richasf.model.LoanItem;

class LoanItemView {

    @FunctionalInterface
    interface DeleteHandler {
        void delete(LoanItem item);
    }

    private final JPanel panel = new JPanel();
    private Consumer<LoanItem> deleteHandler = item -> {};

    LoanItemView(LoanItem item) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        var type = "Type: " + item.getTypeAsString();
        var name = item.getName();
        var publishedBy = "Published by " + item.getPublisher();
        var loanedTo = "Loaned to " + item.getLoanedTo();
        var due = new StringBuilder("Due on ").append(item.getDue()).append(" (");
        var now = LocalDate.now();
        var dueCompare = item.getDue().compareTo(now);
        if (dueCompare > 0) {
            due.append("due in ")
                    .append(now.until(item.getDue(), ChronoUnit.DAYS))
                    .append(" day(s)");
        } else if (dueCompare < 0) {
            due.append("overdue by ")
                    .append(item.getDue().until(now, ChronoUnit.DAYS))
                    .append(" day(s)");
        } else {
            due.append("due today");
        }
        due.append(")");

        panel.add(new JLabel(type));
        panel.add(new JLabel(name));
        panel.add(new JLabel(publishedBy));
        panel.add(new JLabel(loanedTo));
        panel.add(new JLabel(due.toString()));

        var removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> deleteHandler.accept(item));

        panel.add(removeButton);
    }

    JPanel getPanel() {
        return panel;
    }

    void setDeleteHandler(Consumer<LoanItem> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

}
