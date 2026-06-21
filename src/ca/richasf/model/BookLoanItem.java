package ca.richasf.model;

import java.time.LocalDate;

/**
 * A loan for a book.
 */
public final class BookLoanItem extends LoanItem {

    private final int pageCount;

    /**
     * Constructs a new book loan item.
     * 
     * @param name      The name of the item.
     * @param publisher The publisher of the item.
     * @param loanedTo  The name loaned to.
     * @param due       When the loan is due.
     * @param pageCount The page count of the book.
     */
    BookLoanItem(String name, String publisher, String loanedTo, LocalDate due, int pageCount) {
        super(name, publisher, loanedTo, due);
        if (pageCount < 0) {
            throw new IllegalArgumentException("pageCount must not be negative.");
        }
        this.pageCount = pageCount;
    }

    /**
     * Get the page count.
     * 
     * @return The page count.
     */
    public int getPageCount() {
        return pageCount;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(super.toString())
                .append("- ").append(pageCount).append(" pages").append('\n')
                .toString();
    }
}