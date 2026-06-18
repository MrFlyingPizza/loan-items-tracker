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
    private final Map<String, Supplier<LoanItem>> PROMPTS = Map.of(
            "book", this::promptBookLoanItem,
            "audio", this::promptAudioLoanItem,
            "movie", this::promptMovieLoanItem);

    public Optional<LoanItem> getInstance(String type) {
        return Optional.ofNullable(PROMPTS.get(type)).map(Supplier::get);
    }

    public BookLoanItem promptBookLoanItem() {
        return null; // TODO
    }

    public AudioLoanItem promptAudioLoanItem() {
        return null; // TODO
    }

    public MovieLoanItem promptMovieLoanItem() {
        return null; // TODO
    }
}
