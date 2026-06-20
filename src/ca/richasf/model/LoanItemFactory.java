package ca.richasf.model;

import java.time.Duration;
import java.time.LocalDate;

/**
 * A factory that prompts the user to create loan items.
 */
public final class LoanItemFactory {
    /**
     * Data necessary to construct a {@link LoanItem}.
     */
    public record Loan(String to, LocalDate due) {
    }

    /**
     * Data necessary to construct a {@link BookLoanItem}.
     */
    public record Book(String name, String publisher, int pageCount) {
    }

    /**
     * Data necessary to construct a {@link AudioLoanItem}.
     */
    public record Audio(String name, String publisher, Duration duration) {
    }

    /**
     * Data necessary to construct a {@link VideoLoanItem}.
     */
    public record Video(String name, String publisher, String genre) {
    }

    /**
     * Constructs a new loan factory.
     */
    public LoanItemFactory() {

    }

    /**
     * Creates a new book loan item.
     * 
     * @param book The book information.
     * @param loan The loan information.
     * @return The new book loan item.
     */
    public BookLoanItem getInstance(Book book, Loan loan) {
        return new BookLoanItem(book.name, book.publisher, loan.to, loan.due, book.pageCount);
    }

    /**
     * Creates a new audio loan item.
     * 
     * @param book The audio information.
     * @param loan The loan information.
     * @return The new audio loan item.
     */
    public AudioLoanItem getInstance(Audio audio, Loan loan) {
        return new AudioLoanItem(audio.name, audio.publisher, loan.to, loan.due, audio.duration);
    }

    /**
     * Creates a new video loan item.
     * 
     * @param book The video information.
     * @param loan The loan information.
     * @return The new video loan item.
     */
    public VideoLoanItem getInstance(Video video, Loan loan) {
        return new VideoLoanItem(video.name, video.publisher, loan.to, loan.due, video.genre);
    }

}
