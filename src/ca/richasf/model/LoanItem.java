package ca.richasf.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a loan that has been placed by someone for something.
 */
public class LoanItem implements Comparable<LoanItem> {

    private final String name, publisher, loanedTo;
    private final LocalDate due;

    /**
     * Constructs a new loan item.
     * 
     * @param name      The name of the item.
     * @param publisher The publisher of the item.
     * @param loanedTo  The name loaned to.
     * @param due       When the loan is due.
     */
    LoanItem(String name, String publisher, String loanedTo, LocalDate due) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(publisher);
        Objects.requireNonNull(loanedTo);
        Objects.requireNonNull(due);

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank.");
        }

        if (loanedTo.isBlank()) {
            throw new IllegalArgumentException("loanedTo must not be blank.");
        }

        this.name = name;
        this.publisher = publisher;
        this.loanedTo = loanedTo;
        this.due = due;
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
     * Retrieves the publisher of the loaned item.
     * 
     * @return The publisher.
     */
    public String getPublisher() {
        return publisher;
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
     * Get when the loan is due.
     * 
     * @return The due date.
     */
    public LocalDate getDue() {
        return due;
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
