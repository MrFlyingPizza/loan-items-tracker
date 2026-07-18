package ca.richasf.view.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.VideoLoanItem;

/**
 * Displays a loan item and allows delete.
 */
class LoanItemView {

    @FunctionalInterface
    interface DeleteHandler {
        void delete(LoanItem item);
    }

    private final LoanItem loanItem;
    private final JPanel panel = new JPanel();
    private Consumer<LoanItem> deleteHandler = item -> {
    };

    /**
     * Constructs a new loan item view.
     * 
     * @param loanItem The item to show.
     */
    LoanItemView(LoanItem loanItem) {
        this.loanItem = loanItem;

        panel.setLayout(new BorderLayout());

        panel.add(createContentPanel(), BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.PAGE_END);
    }

    /**
     * Create the panel that shows the item's content.
     * 
     * @return The panel.
     */
    private JPanel createContentPanel() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        var type = "Type: " + loanItem.getTypeAsString();
        var name = loanItem.getName();
        var publishedBy = "Published by " + loanItem.getPublisher();
        var loanedTo = "Loaned to " + loanItem.getLoanedTo();
        var due = new StringBuilder("Due on ").append(loanItem.getDue()).append(" (");
        var now = LocalDate.now();
        var dueCompare = loanItem.getDue().compareTo(now);
        if (dueCompare > 0) {
            due.append("due in ")
                    .append(now.until(loanItem.getDue(), ChronoUnit.DAYS))
                    .append(" day(s)");
        } else if (dueCompare < 0) {
            due.append("overdue by ")
                    .append(loanItem.getDue().until(now, ChronoUnit.DAYS))
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

        switch (loanItem) {
            case BookLoanItem book -> {
                panel.add(new JLabel(book.getPageCount() + " pages"));
            }
            case AudioLoanItem audio -> {
                var duration = audio.getDuration();
                var durationText = new StringBuilder()
                        .append(duration.toHours())
                        .append(" hour(s) ")
                        .append(duration.toMinutesPart())
                        .append(" minute(s) ")
                        .append(duration.toSecondsPart())
                        .append(" second(s) long").toString();
                panel.add(new JLabel(durationText));
            }
            case VideoLoanItem video -> {
                panel.add(new JLabel(video.getGenre() + " genre"));
            }
            default -> {
            }
        }

        return panel;
    }

    /**
     * Create the panel that hosts buttons.
     * 
     * @return The panel.
     */
    private JPanel createButtonPanel() {
        var panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        var removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> deleteHandler.accept(loanItem));

        panel.add(removeButton);

        return panel;
    }

    /**
     * Get the panel of this view.
     * 
     * @return The panel.
     */
    JPanel getPanel() {
        return panel;
    }

    /**
     * Set the delete handler for this view.
     * 
     * @param deleteHandler The handler.
     */
    void setDeleteHandler(Consumer<LoanItem> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

}
