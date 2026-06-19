package ca.richasf.model;

import java.time.Duration;

public final class AudioLoanItem extends LoanItem {
    private Duration length;

    public AudioLoanItem() {
    }
    
    /**
     * Get the audio length.
     * @return The audio length.
     */
    public Duration getLength() {
        return length;
    }

    /**
     * Set the audio length.
     * @param length The length of the audio.
     */
    public void setLength(Duration length) {
        this.length = length;
    }
}
