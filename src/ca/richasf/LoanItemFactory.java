package ca.richasf;

import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.MovieLoanItem;

/**
 * A factory that prompts the user to create loan items.
 */
public final class LoanItemFactory {
    public static final String BOOK = "book", AUDIO = "audio", MOVIE = "movie";

    public static LoanItem getInstance(String type) throws UnknownLoanItemTypeException {
        return switch (type) {
            case BOOK -> promptBookLoanItem();
            case AUDIO -> promptAudioLoanItem();
            case MOVIE -> promptMovieLoanItem();
            default -> throw new UnknownLoanItemTypeException();
        };
    }

    private static BookLoanItem promptBookLoanItem() {
        return null; // TODO
    }

    private static AudioLoanItem promptAudioLoanItem() {
        return null; // TODO
    }

    private static MovieLoanItem promptMovieLoanItem() {
        return null; // TODO
    }
}
