package ca.richasf.model;

import java.time.LocalDate;

public final class BookLoanItem extends LoanItem {

    private final int pageCount;

    BookLoanItem(String name, String publisher, String loanedTo, LocalDate due, int pageCount) {
        super(name, publisher, loanedTo, due);
        this.pageCount = pageCount;
    }
    
    public int getPageCount() {
        return pageCount;
    }
}
