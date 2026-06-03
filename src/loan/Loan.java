package loan;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a loan that has been placed by someone for something.
 */
public class Loan {
    /**
     * Creates a new loan.
     * 
     * @param name      The name of the loaned item.
     * @param publisher The publisher of the loaned item.
     * @param loanedTo  The person that the item was loaned to.
     * @param due       When the loan is due for return.
     */
    public Loan(String name, String publisher, String loanedTo, LocalDate due) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(publisher);
        Objects.requireNonNull(loanedTo);
        Objects.requireNonNull(due);

        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank.");
        }

        if (loanedTo.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank.");
        }

        this.name = name;
        this.publisher = publisher;
        this.loanedTo = loanedTo;
        this.due = due;
    }

    private final String name, publisher, loanedTo;
    private final LocalDate due;

    @Override
    public String toString() {
        return "Loan [name=" + name + ", publisher=" + publisher + ", loanedTo=" + loanedTo + ", due=" + due + "]";
    }

    public String getName() {
        return name;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getLoanedTo() {
        return loanedTo;
    }

    public LocalDate getDue() {
        return due;
    }
}
