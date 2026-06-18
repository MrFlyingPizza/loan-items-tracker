package ca.richasf;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import ca.richasf.model.AudioLoanItem;
import ca.richasf.model.BookLoanItem;
import ca.richasf.model.LoanItem;
import ca.richasf.model.MovieLoanItem;

/**
 * A factory that prompts the user to create loan items.
 */
public final class LoanItemFactory {
    public static final String BOOK = "book", AUDIO = "audio", MOVIE = "movie";

    private static final Map<String, Supplier<LoanItem>> PROMPTS = Map.of(
                BOOK, LoanItemFactory::promptBookLoanItem,
                AUDIO, LoanItemFactory::promptAudioLoanItem,
                MOVIE, LoanItemFactory::promptMovieLoanItem);

    public static Optional<LoanItem> getInstance(String type) {
        return Optional.ofNullable(PROMPTS.get(type)).map(Supplier::get);
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
