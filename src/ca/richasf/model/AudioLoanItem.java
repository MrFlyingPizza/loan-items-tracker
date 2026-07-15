package ca.richasf.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A loan for an audio.
 */
public final class AudioLoanItem extends LoanItem {
    private final Duration duration;

    /**
     * Constructs a new audio loan item.
     * 
     * @param name      The name of the item.
     * @param publisher The publisher of the item.
     * @param loanedTo  The name loaned to.
     * @param due       When the loan is due.
     * @param duration  The audio length.
     */
    AudioLoanItem(String name, String publisher, String loanedTo, LocalDate due, Duration duration) {
        super(name, publisher, loanedTo, due);
        Objects.requireNonNull(duration);
        this.duration = duration;
    }

    /**
     * Should be "Audio".
     */
    @Override
    public String getTypeAsString() {
        return "Audio";
    }

    /**
     * Get the audio length.
     * 
     * @return The audio length.
     */
    public Duration getDuration() {
        return duration;
    }
}
