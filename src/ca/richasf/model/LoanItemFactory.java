package ca.richasf.model;

import java.time.LocalDate;

public class LoanItemFactory {
    public LoanItem getInstance(
        String name,
        String publisher,
        String loanedTo,
        LocalDate due
    ) {
        return new BasicLoanItem(name, publisher, loanedTo, due);
    }
}
