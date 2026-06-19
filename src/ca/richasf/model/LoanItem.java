package ca.richasf.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a loan that has been placed by someone for something.
 */
public class LoanItem implements Comparable<LoanItem> {

    private String name, publisher, loanedTo;
    private LocalDate due;

    public LoanItem() {
    }

    /**
     * Retrieves the name of the loaned item.
     * 
     * @return The name of the loaned item.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the loaned item.
     * 
     * @param name The new name of the loaned item.
     */
    public void setName(String name) {
        Objects.requireNonNull(name);

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank.");
        }
        this.name = name;
    }

    /**
     * Retrieves the publisher of the loaned item.
     * 
     * @return The publisher.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Set the publisher of the loaned item.
     * 
     * @param publisher The publisher.
     */
    public void setPublisher(String publisher) {
        Objects.requireNonNull(publisher);
        this.publisher = publisher;
    }

    /**
     * Retrieve who the item was loaned to.
     * 
     * @return Name of the loaned-to person.
     */
    public String getLoanedTo() {
        return loanedTo;
    }

    /**
     * Set the loaned-to name.
     * 
     * @param loanedTo The new loaned-to name.
     */
    public void setLoanedTo(String loanedTo) {
        Objects.requireNonNull(loanedTo);

        if (loanedTo.isBlank()) {
            throw new IllegalArgumentException("Loaned to must not be blank.");
        }
        this.loanedTo = loanedTo;
    }

    /**
     * Get when the loan is due.
     * 
     * @return The due date.
     */
    public LocalDate getDue() {
        return due;
    }

    /**
     * Set the due date.
     * 
     * @param due The new due date.
     */
    public void setDue(LocalDate due) {
        Objects.requireNonNull(due);
        this.due = due;
    }

    /**
     * Convert to string representation.
     */
    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(name)
                .append("\n- published by ").append(publisher)
                .append("\n- loaned to ").append(loanedTo)
                .append("\n- due on ").append(due).append(' ');

        builder.append('(');

        var daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), due);
        if (daysUntilDue > 0) {
            builder.append("due in ")
                    .append(daysUntilDue)
                    .append(" days(s)");
        } else if (daysUntilDue < 0) {
            builder.append("overdue by ")
                    .append(-daysUntilDue)
                    .append(" days(s)");
        } else {
            builder.append("due today");
        }

        builder.append(')');

        return builder.toString();
    }

    @Override
    public int compareTo(LoanItem other) {
        return getDue().compareTo(other.getDue());
    }

}
