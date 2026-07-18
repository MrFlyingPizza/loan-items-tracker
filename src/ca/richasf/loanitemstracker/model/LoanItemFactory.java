package ca.richasf.loanitemstracker.model;

import java.time.Duration;
import java.time.LocalDate;

/**
 * A factory that creates loan items.
 */
public final class LoanItemFactory {
    /**
     * Data necessary to construct a {@link LoanItem}.
     * 
     * @param to  Who the loan is for.
     * @param due When the loan is due.
     */
    public record Loan(String to, LocalDate due) {
    }

    /**
     * Data necessary to construct a {@link BookLoanItem}.
     * 
     * @param name      The name of the book.
     * @param publisher The publisher of the book.
     * @param pageCount The number of pages of the book.
     */
    public record Book(String name, String publisher, int pageCount) {
    }

    /**
     * Data necessary to construct a {@link AudioLoanItem}.
     * 
     * @param name      The name of the audio.
     * @param publisher The publisher of the audio.
     * @param duration  How long the audio is.
     */
    public record Audio(String name, String publisher, Duration duration) {
    }

    /**
     * Data necessary to construct a {@link VideoLoanItem}.
     * 
     * @param name      The name of the video.
     * @param publisher The publisher of the video.
     * @param genre     The genre of the video.
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
     * @param audio The audio information.
     * @param loan  The loan information.
     * @return The new audio loan item.
     */
    public AudioLoanItem getInstance(Audio audio, Loan loan) {
        return new AudioLoanItem(audio.name, audio.publisher, loan.to, loan.due, audio.duration);
    }

    /**
     * Creates a new video loan item.
     * 
     * @param video The video information.
     * @param loan  The loan information.
     * @return The new video loan item.
     */
    public VideoLoanItem getInstance(Video video, Loan loan) {
        return new VideoLoanItem(video.name, video.publisher, loan.to, loan.due, video.genre);
    }

}
