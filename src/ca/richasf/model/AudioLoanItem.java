package ca.richasf.model;

import java.time.LocalDate;

public final class AudioLoanItem extends LoanItem {
    private final int length;

    AudioLoanItem(String name, String publisher, String loanedTo, LocalDate due, int length) {
        super(name, publisher, loanedTo, due);
        this.length = length;
    }
    
    public int getLength() {
        return length;
    }
}
