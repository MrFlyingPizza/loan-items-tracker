package ca.richasf.model;

import java.time.LocalDate;

public final class MovieLoanItem extends LoanItem {
    private final String genre;

    MovieLoanItem(String name, String publisher, String loanedTo, LocalDate due, String genre) {
        super(name, publisher, loanedTo, due);
        this.genre = genre;
    }
    
    public String getGenre() {
        return genre;
    }
}
