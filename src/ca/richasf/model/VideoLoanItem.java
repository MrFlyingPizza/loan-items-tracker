package ca.richasf.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A loan for a video.
 */
public final class VideoLoanItem extends LoanItem {
    private final String genre;

    /**
     * Constructs a new video loan item.
     * 
     * @param name      The name of the item.
     * @param publisher The publisher of the item.
     * @param loanedTo  The name loaned to.
     * @param due       When the loan is due.
     * @param genre     The genre of the video.
     */
    VideoLoanItem(String name, String publisher, String loanedTo, LocalDate due, String genre) {
        super(name, publisher, loanedTo, due);
        Objects.requireNonNull(genre);
        if (genre.isBlank()) {
            throw new IllegalArgumentException("genre must not be blank.");
        }
        this.genre = genre;
    }

    /**
     * Get the video's genre.
     * 
     * @return The genre.
     */
    public String getGenre() {
        return genre;
    }
}
