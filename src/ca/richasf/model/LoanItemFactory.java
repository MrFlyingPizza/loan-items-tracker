package ca.richasf.model;

import java.time.LocalDate;

public class LoanItemFactory {
    public record Book(
            String name,
            String publisher,
            String loanedTo,
            LocalDate due,
            int pageCount) {
    };

    public record Audio(
            String name,
            String publisher,
            String loanedTo,
            LocalDate due,
            int length) {
    };

    public record Movie(
            String name,
            String publisher,
            String loanedTo,
            LocalDate due,
            String genre) {
    };
    
    public LoanItem getInstance(
            String name,
            String publisher,
            String loanedTo,
            LocalDate due) {
        return new LoanItem(
                name,
                publisher,
                loanedTo,
                due) {};
    }

    public BookLoanItem getInstance(Book book) {
        return new BookLoanItem(
                book.name,
                book.publisher,
                book.loanedTo,
                book.due,
                book.pageCount);
    }

    public AudioLoanItem getInstance(Audio audio) {
        return new AudioLoanItem(
                audio.name,
                audio.publisher,
                audio.loanedTo,
                audio.due,
                audio.length);
    }

    public MovieLoanItem getInstance(Movie movie) {
        return new MovieLoanItem(
                movie.name,
                movie.publisher,
                movie.loanedTo,
                movie.due,
                movie.genre);
    }
}
