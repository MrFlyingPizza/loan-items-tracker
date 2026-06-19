package ca.richasf.model;

public final class VideoLoanItem extends LoanItem {
    private String genre;

    public VideoLoanItem() {
    }
    
    /**
     * Get the video's genre.
     * @return The genre.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Set the video's genre.
     * @param genre The new genre.
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }
}
